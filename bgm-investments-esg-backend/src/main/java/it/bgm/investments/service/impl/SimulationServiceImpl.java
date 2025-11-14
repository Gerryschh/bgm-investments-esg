package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.RunSimulationBodyModel;
import it.bgm.investments.api.model.SimulationResponseModel;
import it.bgm.investments.domain.Asset;
import it.bgm.investments.domain.Portfolio;
import it.bgm.investments.domain.PortfolioPosition;
import it.bgm.investments.domain.Simulation;
import it.bgm.investments.mapper.SimulationMapper;
import it.bgm.investments.repo.PortfolioPositionRepository;
import it.bgm.investments.repo.PortfolioRepository;
import it.bgm.investments.repo.SimulationRepository;
import it.bgm.investments.security.AuthFacade;
import it.bgm.investments.service.SimulationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private static final int MONTE_CARLO_SIMULATIONS = 10_000;

    private final PortfolioRepository portfolioRepo;
    private final PortfolioPositionRepository positions;
    private final SimulationRepository sims;
    private final SimulationMapper mapper;
    private final AuthFacade auth;

    @Override
    public SimulationResponseModel run(Long portfolioId, RunSimulationBodyModel body, String jSessionId) {
        // 1) Ownership
        Long userId = auth.userId(jSessionId);
        Portfolio p = portfolioRepo.findOwned(portfolioId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio " + portfolioId + " non trovato o non posseduto"));

        // 2) Carica posizioni del portafoglio
        List<PortfolioPosition> pos = positions.findByPortfolioId(p.getId());
        if (pos == null || pos.isEmpty()) {
            throw new IllegalArgumentException("Nessuna posizione");
        }

        // 3) Valore totale portafoglio (prezzo * quantità)
        BigDecimal total = BigDecimal.ZERO;
        for (PortfolioPosition pp : pos) {
            BigDecimal prezzo = pp.getAsset().getPrezzo();
            BigDecimal qty = pp.getQuantita();
            if (prezzo == null || qty == null) continue;
            total = total.add(prezzo.multiply(qty));
        }
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Nessuna posizione");
        }

        // 4) Calcolo pesi w_i = (prezzo_i * quantita_i) / totale
        List<W> ws = new ArrayList<>(pos.size());
        for (PortfolioPosition pp : pos) {
            Asset a = pp.getAsset();
            BigDecimal prezzo = a.getPrezzo();
            BigDecimal qty = pp.getQuantita();
            if (prezzo == null || qty == null) continue;
            BigDecimal val = prezzo.multiply(qty);
            BigDecimal w = val.divide(total, 8, RoundingMode.HALF_UP);
            ws.add(new W(a, w));
        }

        // 5) Calcolo ESG medio pesato
        BigDecimal esg = BigDecimal.ZERO;
        for (W w : ws) {
            Integer esgAsset = w.a.getEsg();
            if (esgAsset == null) continue;
            esg = esg.add(BigDecimal.valueOf(esgAsset.intValue()).multiply(w.w));
        }

        // 6) Rendimento medio mensile pesato r
        BigDecimal r = BigDecimal.ZERO;
        for (W w : ws) {
            BigDecimal rm = w.a.getRendimentoMensile();
            if (rm == null) continue;
            r = r.add(rm.multiply(w.w));
        }

        // 7) Volatilità mensile aggregata sigma = sqrt( sum ( w^2 * sigma_i^2 ) )
        BigDecimal v2sum = BigDecimal.ZERO;
        for (W w : ws) {
            BigDecimal sigma_i = w.a.getVolatilitaMensile();
            if (sigma_i == null) continue;
            BigDecimal term = w.w.multiply(w.w).multiply(sigma_i.multiply(sigma_i));
            v2sum = v2sum.add(term);
        }
        BigDecimal sigma = v2sum.sqrt(new MathContext(8));

        int mesi = body.getMesi();

        // 8) Monte Carlo su orizzonte "mesi"
        MonteCarloResult mc = runMonteCarlo(r, sigma, mesi, MONTE_CARLO_SIMULATIONS);

        // 9) Persisti Simulation e restituisci risposta
        Simulation s = new Simulation();
        s.setPortfolio(p);
        s.setMesi(mesi);
        s.setEsg(esg);
        s.setRendimentoAtteso(mc.getMean());      // rendimento atteso finale simulato
        s.setVolatilita(sigma);                   // volatilità mensile del portafoglio
        s.setScenarioNeutro(mc.getMedian());      // mediana
        s.setScenarioOttimistico(mc.getP95());    // 95° percentile
        s.setScenarioPessimistico(mc.getP05());   // 5° percentile

        Simulation saved = sims.save(s);
        return mapper.toResponse(saved);
    }

    /**
     * Esegue una simulazione Monte Carlo sui rendimenti del portafoglio.
     *
     * @param monthlyReturn rendimento medio mensile del portafoglio (BigDecimal, es. 0.01 = 1%)
     * @param monthlyVol    volatilità mensile del portafoglio (BigDecimal, es. 0.05 = 5%)
     * @param months        orizzonte temporale in mesi
     * @param simulations   numero di scenari da generare
     */
    private MonteCarloResult runMonteCarlo(BigDecimal monthlyReturn,
                                           BigDecimal monthlyVol,
                                           int months,
                                           int simulations) {

        double mu = monthlyReturn.doubleValue();
        double sigma = monthlyVol.doubleValue();

        double[] finalReturns = new double[simulations];
        Random random = new Random();

        for (int s = 0; s < simulations; s++) {
            double portfolioValue = 1.0; // partiamo da 1

            for (int m = 0; m < months; m++) {
                // Genera una variabile normale standard Z ~ N(0,1) (Box-Muller)
                double z = nextStandardNormal(random);

                // Rendimento mensile simulato: R = mu + sigma * Z
                double monthlySim = mu + sigma * z;

                // Aggiorna il valore del portafoglio
                portfolioValue *= (1.0 + monthlySim);
            }

            // Rendimento totale su "months" mesi
            finalReturns[s] = portfolioValue - 1.0;
        }

        // Ordiniamo per calcolare percentile e mediana
        Arrays.sort(finalReturns);

        double sum = 0.0;
        for (double r : finalReturns) {
            sum += r;
        }
        double mean = sum / simulations;

        double p05 = finalReturns[(int) Math.floor(0.05 * simulations)];
        double median = finalReturns[(int) Math.floor(0.50 * simulations)];
        double p95 = finalReturns[(int) Math.floor(0.95 * simulations)];

        return new MonteCarloResult(
                BigDecimal.valueOf(mean),
                BigDecimal.valueOf(median),
                BigDecimal.valueOf(p05),
                BigDecimal.valueOf(p95)
        );
    }

    /**
     * Genera una variabile casuale Z ~ N(0,1) usando Box-Muller.
     */
    private double nextStandardNormal(Random random) {
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();
        // Box-Muller transform
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
    }

    private static final class MonteCarloResult {
        private final BigDecimal mean;
        private final BigDecimal median;
        private final BigDecimal p05;
        private final BigDecimal p95;

        private MonteCarloResult(BigDecimal mean, BigDecimal median, BigDecimal p05, BigDecimal p95) {
            this.mean = mean;
            this.median = median;
            this.p05 = p05;
            this.p95 = p95;
        }

        public BigDecimal getMean() { return mean; }
        public BigDecimal getMedian() { return median; }
        public BigDecimal getP05() { return p05; }
        public BigDecimal getP95() { return p95; }
    }

    //UTILITY
    private static final class W {
        private final Asset a;
        private final BigDecimal w;

        private W(Asset a, BigDecimal w) {
            this.a = a;
            this.w = w;
        }
    }
}
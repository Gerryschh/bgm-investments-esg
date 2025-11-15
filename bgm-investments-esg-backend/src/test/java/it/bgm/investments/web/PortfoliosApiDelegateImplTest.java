package it.bgm.investments.web;

import it.bgm.investments.api.model.*;
import it.bgm.investments.service.PortfolioService;
import it.bgm.investments.service.PositionService;
import it.bgm.investments.service.SimulationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfoliosApiDelegateImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private PositionService positionService;

    @Mock
    private SimulationService simulationService;

    @InjectMocks
    private PortfoliosApiDelegateImpl delegate;

    @Test
    void getMyPortfolios_usesCurrentSession() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        PortfolioListResponseModel list = new PortfolioListResponseModel();
        when(portfolioService.myPortfolios("TOKEN")).thenReturn(list);

        ResponseEntity<PortfolioListResponseModel> res = delegate.getMyPortfolios();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(list);
        verify(portfolioService).myPortfolios("TOKEN");
    }

    @Test
    void createPortfolio_returns201() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        CreatePortfolioBodyModel body = new CreatePortfolioBodyModel();
        PortfolioResponseModel out = new PortfolioResponseModel();
        when(portfolioService.create("TOKEN", body)).thenReturn(out);

        ResponseEntity<PortfolioResponseModel> res = delegate.createPortfolio(body);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isSameAs(out);
        verify(portfolioService).create("TOKEN", body);
    }

    @Test
    void getPortfolioPositions_returnsOk() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        PositionListResponseModel list = new PositionListResponseModel();
        when(positionService.list(1L, "TOKEN")).thenReturn(list);

        ResponseEntity<PositionListResponseModel> res = delegate.getPortfolioPositions(1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(list);
        verify(positionService).list(1L, "TOKEN");
    }

    @Test
    void addPosition_returns201() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        CreatePositionBodyModel body = new CreatePositionBodyModel();
        PositionResponseModel out = new PositionResponseModel();
        when(positionService.add(1L, body, "TOKEN")).thenReturn(out);

        ResponseEntity<PositionResponseModel> res = delegate.addPosition(body, 1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isSameAs(out);
        verify(positionService).add(1L, body, "TOKEN");
    }

    @Test
    void removePosition_returns204() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");

        ResponseEntity<Void> res = delegate.removePosition(1L, 2L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(positionService).remove(1L, 2L, "TOKEN");
    }

    @Test
    void runSimulation_returnsOk() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        RunSimulationBodyModel body = new RunSimulationBodyModel();
        SimulationResponseModel out = new SimulationResponseModel();
        when(simulationService.run(1L, body, "TOKEN")).thenReturn(out);

        ResponseEntity<SimulationResponseModel> res = delegate.runSimulation(body, 1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(out);
        verify(simulationService).run(1L, body, "TOKEN");
    }
}
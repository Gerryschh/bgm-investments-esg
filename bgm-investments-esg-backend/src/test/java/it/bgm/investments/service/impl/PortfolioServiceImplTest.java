package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.CreatePortfolioBodyModel;
import it.bgm.investments.api.model.PortfolioListResponseModel;
import it.bgm.investments.api.model.PortfolioModel;
import it.bgm.investments.api.model.PortfolioResponseModel;
import it.bgm.investments.domain.Portfolio;
import it.bgm.investments.domain.User;
import it.bgm.investments.mapper.PortfolioMapper;
import it.bgm.investments.repo.PortfolioRepository;
import it.bgm.investments.repo.UserRepository;
import it.bgm.investments.security.AuthFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository repo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PortfolioMapper mapper;

    @Mock
    private AuthFacade auth;

    @InjectMocks
    private PortfolioServiceImpl service;

    @Test
    void myPortfolios_usesAuthAndRepo() {
        String token = "token123";
        Long userId = 42L;

        Portfolio p = new Portfolio();
        p.setId(1L);

        PortfolioModel model = new PortfolioModel();

        when(auth.userId(token)).thenReturn(userId);
        when(repo.findByOwnerId(userId)).thenReturn(List.of(p));
        when(mapper.toModel(p)).thenReturn(model);

        PortfolioListResponseModel result = service.myPortfolios(token);

        verify(auth).userId(token);
        verify(repo).findByOwnerId(userId);
        verify(mapper).toModel(p);
        verifyNoInteractions(userRepo);

        assertThat(result).isNotNull();
    }

    @Test
    void create_createsPortfolioForAuthenticatedUser() {
        String token = "token123";
        Long userId = 42L;

        User owner = new User();
        owner.setId(userId);

        CreatePortfolioBodyModel body = new CreatePortfolioBodyModel();
        body.setNome("My portfolio");

        Portfolio saved = new Portfolio();
        saved.setId(99L);

        PortfolioResponseModel response = new PortfolioResponseModel();

        when(auth.userId(token)).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(owner));
        when(repo.save(any(Portfolio.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        PortfolioResponseModel res = service.create(token, body);

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        verify(repo).save(captor.capture());
        Portfolio toSave = captor.getValue();

        assertThat(toSave.getNome()).isEqualTo("My portfolio");
        assertThat(toSave.getOwner()).isEqualTo(owner);
        assertThat(res).isSameAs(response);

        verify(auth).userId(token);
        verify(userRepo).findById(userId);
    }
}
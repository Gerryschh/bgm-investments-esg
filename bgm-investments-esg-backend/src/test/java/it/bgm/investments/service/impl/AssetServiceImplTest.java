package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.AssetListResponseModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.api.model.CreateAssetBodyModel;
import it.bgm.investments.api.model.UpdateAssetBodyModel;
import it.bgm.investments.domain.Asset;
import it.bgm.investments.mapper.AssetMapper;
import it.bgm.investments.repo.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetRepository repo;

    @Mock
    private AssetMapper mapper;

    @InjectMocks
    private AssetServiceImpl service;

    @Test
    void list_usesRepositorySearch() {
        when(repo.search(true, "EQUITY")).thenReturn(List.of(new Asset()));

        AssetListResponseModel res = service.list(true, "EQUITY");

        verify(repo).search(true, "EQUITY");
        assertThat(res).isNotNull();
    }

    @Test
    void get_returnsMappedAsset() {
        Asset a = new Asset();
        a.setId(1L);
        AssetResponseModel dto = new AssetResponseModel();

        when(repo.findById(1L)).thenReturn(Optional.of(a));
        when(mapper.toResponse(a)).thenReturn(dto);

        AssetResponseModel result = service.get(1L);

        verify(repo).findById(1L);
        verify(mapper).toResponse(a);
        assertThat(result).isSameAs(dto);
    }

    @Test
    void create_persistsAndReturnsAsset() {
        CreateAssetBodyModel body = new CreateAssetBodyModel();
        Asset entity = new Asset();
        Asset saved = new Asset();
        AssetResponseModel dto = new AssetResponseModel();

        when(mapper.fromModel(any())).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(dto);

        AssetResponseModel result = service.create(body);

        verify(repo).save(entity);
        assertThat(result).isSameAs(dto);
    }

    @Test
    void update_updatesExistingAsset() {
        UpdateAssetBodyModel body = new UpdateAssetBodyModel();
        Asset existing = new Asset();
        Asset saved = new Asset();
        AssetResponseModel dto = new AssetResponseModel();

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(dto);

        AssetResponseModel result = service.update(1L, body);

        verify(repo).findById(1L);
        verify(repo).save(existing);
        assertThat(result).isSameAs(dto);
    }

    @Test
    void deactivate_marksAssetInactive() {
        Asset existing = new Asset();
        existing.setActive(true);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        service.deactivate(1L);

        assertThat(existing.getActive()).isFalse();
        verify(repo).save(existing);
    }
}
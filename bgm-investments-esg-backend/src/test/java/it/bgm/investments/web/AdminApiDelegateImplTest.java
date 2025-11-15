package it.bgm.investments.web;

import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.api.model.CreateAssetBodyModel;
import it.bgm.investments.api.model.UpdateAssetBodyModel;
import it.bgm.investments.service.AssetService;
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
class AdminApiDelegateImplTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AdminApiDelegateImpl delegate;

    @Test
    void createAsset_returns201AndBody() {
        CreateAssetBodyModel body = new CreateAssetBodyModel();
        AssetResponseModel response = new AssetResponseModel();

        when(assetService.create(body)).thenReturn(response);

        ResponseEntity<AssetResponseModel> res = delegate.createAsset(body);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).isSameAs(response);
        verify(assetService).create(body);
    }

    @Test
    void updateAsset_returns200AndBody() {
        UpdateAssetBodyModel body = new UpdateAssetBodyModel();
        AssetResponseModel response = new AssetResponseModel();

        when(assetService.update(1L, body)).thenReturn(response);

        ResponseEntity<AssetResponseModel> res = delegate.updateAsset(body, 1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(response);
        verify(assetService).update(1L, body);
    }

    @Test
    void deactivateAsset_returns204() {
        ResponseEntity<Void> res = delegate.deactivateAsset(1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(assetService).deactivate(1L);
    }
}
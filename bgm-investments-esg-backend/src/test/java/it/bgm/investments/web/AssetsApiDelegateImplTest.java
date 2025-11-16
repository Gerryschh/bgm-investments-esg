package it.bgm.investments.web;

import it.bgm.investments.api.model.AssetListResponseModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.service.AssetService;
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
class AssetsApiDelegateImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetsApiDelegateImpl delegate;

    @Test
    void getAssets_delegatesToService() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        AssetListResponseModel list = new AssetListResponseModel();
        when(assetService.list(true, "TECH", "TOKEN")).thenReturn(list);

        ResponseEntity<AssetListResponseModel> res = delegate.getAssets(true, "TECH");

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(list);
        verify(assetService).list(true, "TECH", "TOKEN");
    }

    @Test
    void getAssetById_delegatesToService() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        AssetResponseModel model = new AssetResponseModel();
        when(assetService.get(1L, "TOKEN")).thenReturn(model);

        ResponseEntity<AssetResponseModel> res = delegate.getAssetById(1L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(model);
        verify(assetService).get(1L, "TOKEN");
    }
}
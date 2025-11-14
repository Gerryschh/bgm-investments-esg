package it.bgm.investments.web;

import it.bgm.investments.api.AssetsApiDelegate;
import it.bgm.investments.api.model.AssetListResponseModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetsApiDelegateImpl extends BaseApiDelegate implements AssetsApiDelegate {

    private final AssetService assetService;

    @Override
    public ResponseEntity<AssetListResponseModel> getAssets(Boolean activeOnly, String settore) {
        return ResponseEntity.ok(assetService.list(activeOnly, settore));
    }

    @Override
    public ResponseEntity<AssetResponseModel> getAssetById(Long assetId) {
        return ResponseEntity.ok(assetService.get(assetId));
    }
}

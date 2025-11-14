package it.bgm.investments.web;

import it.bgm.investments.api.AdminApiDelegate;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.api.model.CreateAssetBodyModel;
import it.bgm.investments.api.model.UpdateAssetBodyModel;
import it.bgm.investments.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminApiDelegateImpl extends BaseApiDelegate implements AdminApiDelegate {

    private final AssetService assetService;

    @Override
    public ResponseEntity<AssetResponseModel> createAsset(@Valid CreateAssetBodyModel body) {
        return ResponseEntity.status(201).body(assetService.create(body));
    }

    @Override
    public ResponseEntity<AssetResponseModel> updateAsset(@Valid UpdateAssetBodyModel body, Long assetId) {
        return ResponseEntity.ok(assetService.update(assetId, body));
    }

    @Override
    public ResponseEntity<Void> deactivateAsset(Long assetId) {
        assetService.deactivate(assetId);
        return ResponseEntity.noContent().build();
    }
}

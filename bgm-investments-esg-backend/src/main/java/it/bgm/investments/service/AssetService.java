package it.bgm.investments.service;

import it.bgm.investments.api.model.AssetListResponseModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.api.model.CreateAssetBodyModel;
import it.bgm.investments.api.model.UpdateAssetBodyModel;

public interface AssetService {
    AssetListResponseModel list(Boolean activeOnly, String settore);

    AssetResponseModel get(Long id);

    AssetResponseModel create(CreateAssetBodyModel b);

    AssetResponseModel update(Long id, UpdateAssetBodyModel b);

    void deactivate(Long id);
}

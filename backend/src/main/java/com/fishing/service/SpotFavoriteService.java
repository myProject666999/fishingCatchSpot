package com.fishing.service;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.SpotFavoriteDTO;
import com.fishing.vo.SpotFavoriteVO;

public interface SpotFavoriteService {

    void add(Long userId, SpotFavoriteDTO dto);

    void delete(Long userId, Long favoriteId);

    PageResult<SpotFavoriteVO> listByUser(Long userId, PageQuery query);
}

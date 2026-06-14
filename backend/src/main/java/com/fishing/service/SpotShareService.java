package com.fishing.service;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.SpotShareDTO;
import com.fishing.vo.SpotShareVO;

public interface SpotShareService {

    void share(Long userId, SpotShareDTO dto);

    PageResult<SpotShareVO> list(String keyword, Integer waterType, PageQuery query);

    SpotShareVO getDetail(Long shareId);

    void like(Long shareId);

    void delete(Long userId, Long shareId);
}

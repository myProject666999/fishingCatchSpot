package com.fishing.service;

import com.fishing.vo.CatchDetailVO;

import java.util.List;

public interface CatchDetailService {

    List<CatchDetailVO> getByRecordId(Long recordId);
}

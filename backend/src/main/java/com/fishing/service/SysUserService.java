package com.fishing.service;

import com.fishing.dto.LoginDTO;
import com.fishing.vo.UserVO;

public interface SysUserService {

    UserVO login(LoginDTO dto);
}

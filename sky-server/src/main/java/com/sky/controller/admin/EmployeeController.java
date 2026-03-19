package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {

        System.out.println("EmployeeController.login, 当前线程id:" + Thread.currentThread().getId());

        // 记录登录日志
        log.info("员工登录：{}", employeeLoginDTO);

        // 1、调用业务层进行登录校验（验证用户名和密码是否正确）
        Employee employee = employeeService.login(employeeLoginDTO);

        // 2、登录成功后生成JWT令牌
        // 创建payload部分的数据，用于存储在token中的信息
        Map<String, Object> claims = new HashMap<>();

        // 将当前登录员工的ID存入claims中
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());

        // 调用工具类生成JWT令牌
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),  // JWT签名密钥
                jwtProperties.getAdminTtl(),        // JWT过期时间
                claims);                            // 自定义载荷数据

        // 3、封装返回结果（VO对象），返回给前端
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())                // 员工ID
                .userName(employee.getUsername())    // 用户名
                .name(employee.getName())            // 员工姓名
                .token(token)                        // JWT令牌
                .build();

        // 4、返回统一结果对象
        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }


    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        //返回PageResult
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用、禁用员工账号，1为启用0为禁用
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用员工账号")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("启用、禁用员工账号：status {}, id {}", status, id);
        employeeService.updateStatus(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public  Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 更新员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("更新员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("更新员工信息: {}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

}

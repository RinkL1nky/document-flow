package ru.egartech.documentflow.service.v1;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.egartech.documentflow.dto.v1.request.EmployeeRequestDto;
import ru.egartech.documentflow.dto.v1.request.EmployeeSearchDto;
import ru.egartech.documentflow.dto.v1.response.EmployeeResponseDto;
import ru.egartech.documentflow.dto.v1.response.PageWrapper;

import java.util.List;

public interface EmployeeService extends UserDetailsService {

    /**
     * Получить профиль сотрудника по его идентификатору.
     * @param id ID сотрудника
     * @return DTO для сотрудника
     * @throws ru.egartech.documentflow.exception.NotFoundException если сотрудник не найден
     */
    EmployeeResponseDto getEmployee(Long id);

    /**
     * Найти все профили сотрудников согласно выбранным фильтрам и пагинации.
     * @param searchDto DTO для фильтров
     * @param pageable DTO для параметров пагинации
     * @return результаты поиска (список профилей сотрудников)
     */
    PageWrapper<EmployeeResponseDto> getEmployeePage(EmployeeSearchDto searchDto, Pageable pageable);

    /**
     * Создать аккаунт сотрудника.
     * @param registrationForm DTO для регистрации сотрудника
     * @return созданный профиль в базе данных с его ID
     */
    EmployeeResponseDto createEmployee(EmployeeRequestDto registrationForm);

    /**
     * Обновить данные аккаунта сотрудника.
     * @param employeeId ID сотрудника
     * @param employeeRequestDto DTO для изменения данных аккаунта
     * @throws ru.egartech.documentflow.exception.NotFoundException если сотрудник не найден
     */
    void updateEmployee(Long employeeId, EmployeeRequestDto employeeRequestDto);

    /**
     * Обновить полномочия (роли) сотрудника.
     * @param employeeId ID сотрудника
     * @param employeeAuthorities новый список ролей
     */
    void updateEmployeeAuthorities(Long employeeId, List<String> employeeAuthorities);

    /**
     * Удалить аккаунт сотрудника.
     * @param employeeId ID сотрудника
     * @throws ru.egartech.documentflow.exception.auth.ForbiddenException если пользователь
     * не имеет право удалять аккаунт
     */
    void deleteEmployee(Long employeeId);

}

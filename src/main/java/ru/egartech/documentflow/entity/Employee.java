package ru.egartech.documentflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.egartech.documentflow.converter.EmployeeAuthorityConverter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Employee implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 32)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(min = 8, max = 255)
    private String password;

    @NotBlank
    @Size(min = 3, max = 32)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 32)
    private String lastName;

    @NotBlank
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            flags = {Pattern.Flag.CASE_INSENSITIVE}
    )
    @Column(unique = true)
    private String email;

    @NotNull
    private boolean accountNonExpired = Boolean.TRUE;

    @NotNull
    private boolean accountNonLocked = Boolean.TRUE;

    @NotNull
    private boolean credentialsNonExpired = Boolean.TRUE;

    @NotNull
    private boolean enabled = Boolean.TRUE;

    @NotNull
    @ElementCollection
    @CollectionTable(name = "employee_authority")
    @Convert(converter = EmployeeAuthorityConverter.class)
    @Column(name = "authority_name", columnDefinition = "VARCHAR(255)")
    private Set<GrantedAuthority> authorities = new HashSet<>();

    public enum Authority {
        ROLE_USER, ROLE_ADMIN
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Employee employee = (Employee) o;
        return getId() != null && Objects.equals(getId(), employee.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}

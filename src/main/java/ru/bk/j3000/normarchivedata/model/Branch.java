package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "branches")
public class Branch {
    @Id
    @Column(nullable = false, name = "branch_id")
    @Min(1)
    @Max(30)
    private Integer id;

    @Column(nullable = false, name = "branch_name")
    @Size(max = 30)
    private String branchName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;
        return id.equals(branch.id) && branchName.equals(branch.branchName);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + branchName.hashCode();
        return result;
    }
}

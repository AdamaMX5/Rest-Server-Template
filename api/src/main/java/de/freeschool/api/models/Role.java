package de.freeschool.api.models;

import de.freeschool.api.models.type.RoleType;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "roles")
public class Role {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    public String getNameString() {
        if (name == null) {
            return "No Name Role";
        }
        return name.toString();
    }

    public String toString() {
        return this.getNameString();
    }
}

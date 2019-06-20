package com.mastugin.model;

import java.util.Objects;
import java.util.StringJoiner;

public class TestProjectInfo {

    private String name;

    private String prefix;

    private String description;

    private boolean isRequirements;

    private boolean isActive;

    private boolean isPublic;

    //Не уверен, что корректно инициализировать поля объекта так. Наблюдается некторая магия
    private TestProjectInfo() {
        name = "";
        prefix = "";
        description = "";
        isRequirements = false;
        isPublic = false;
        isActive = false;
    }

    public static TestProjectInfo getInstance() {
        return new TestProjectInfo();
    }

    public TestProjectInfo withName(final String name) {
        this.name = name;
        return this;
    }

    public TestProjectInfo withPrefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    public TestProjectInfo withDescription(final String description) {
        this.description = description;
        return this;
    }

    public TestProjectInfo withIsRequirements(final boolean isRequirements) {
        this.isRequirements = isRequirements;
        return this;
    }

    public TestProjectInfo withIsActive(final boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public TestProjectInfo withIsPublic(final boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }


    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequirements() {
        return isRequirements;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TestProjectInfo.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("prefix='" + prefix + "'")
                .add("description='" + description + "'")
                .add("isRequirements=" + isRequirements)
                .add("isActive=" + isActive)
                .add("isPublic=" + isPublic)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, prefix, description, isRequirements, isActive, isPublic);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TestProjectInfo other = (TestProjectInfo) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.prefix, other.prefix)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.isRequirements, other.isRequirements)
                && Objects.equals(this.isActive, other.isActive)
                && Objects.equals(this.isPublic, other.isPublic);
    }
}

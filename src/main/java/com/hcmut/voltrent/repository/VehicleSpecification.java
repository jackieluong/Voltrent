package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.constant.VehicleType;
import com.hcmut.voltrent.entity.Vehicle;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class VehicleSpecification {

    public static Specification<Vehicle> hasType(VehicleType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), type);
        };
    }

    public static Specification<Vehicle> hasStatus(VehicleStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Vehicle> hasProvince(String province) {
        return (root, query, criteriaBuilder) -> {
            if (province == null || province.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("province"), province);
        };
    }

    public static Specification<Vehicle> hasDistrict(String district) {
        return (root, query, criteriaBuilder) -> {
            if (district == null || district.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("district"), district);
        };
    }

    public static Specification<Vehicle> hasWard(String ward) {
        return (root, query, criteriaBuilder) -> {
            if (ward == null || ward.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("ward"), ward);
        };
    }

    public static Specification<Vehicle> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null || address.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("address"), "%" + address + "%");
        };
    }

    public static Specification<Vehicle> hasPriceBetween(Integer priceMin, Integer priceMax) {
        return (root, query, criteriaBuilder) -> {
            if (priceMin == null && priceMax == null) {
                return criteriaBuilder.conjunction();
            }
            if (priceMin != null && priceMax != null) {
                return criteriaBuilder.between(root.get("pricePerHour"), (double) priceMin, (double) priceMax);
            }
            if (priceMin != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerHour"), (double) priceMin);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("pricePerHour"), (double) priceMax);
        };
    }

    public static Specification<Vehicle> notInIds(List<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.not(root.get("id").in(ids));
        };
    }
}

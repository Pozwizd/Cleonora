package com.example.cleanorarest.mapper;

import com.example.cleanorarest.UploadFile;
import com.example.cleanorarest.entity.AdminRole;
import com.example.cleanorarest.entity.AdminUser;
import com.example.cleanorarest.model.admin.AdminUserProfileRequest;
import com.example.cleanorarest.model.admin.AdminUserProfileResponse;
import com.example.cleanorarest.model.admin.AdminUserRequest;
import com.example.cleanorarest.model.admin.AdminUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.util.List;



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminUserMapper {

    @Mapping(source = "name", target = "name")
    AdminUser adminUserResponsetoEntity(AdminUserRequest adminUserRequest);

    @Mapping(source = "name", target = "name")
    AdminUserResponse adminUsertoResponse(AdminUser adminUser);

    List<AdminUser> toEntityList(List<AdminUserRequest> adminUserRequests);

    List<AdminUserResponse> toResponseList(List<AdminUser> adminUsers);

    default Page<AdminUserResponse> adminUsertoResponsePage(Page<AdminUser> adminUserPage) {
        return adminUserPage.map(this::adminUsertoResponse);
    }

    default AdminUser adminUserProfileRequestToEntity(AdminUserProfileRequest request, UploadFile uploadFile) {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(request.getId());
        adminUser.setName(request.getName());
        adminUser.setSurname(request.getSurname());
        adminUser.setEmail(request.getEmail());
        adminUser.setPhoneNumber(request.getPhoneNumber());
        adminUser.setPassword(request.getPassword());
        adminUser.setIsActive(request.getIsActive());
        adminUser.setRole(AdminRole.valueOf(request.getRole()));

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarPath = uploadFile.uploadFile(request.getAvatar(), null);
            adminUser.setAvatar(avatarPath);
        }

        return adminUser;
    }

    default AdminUserProfileResponse adminUserToProfileResponse(AdminUser adminUser) {
        AdminUserProfileResponse response = new AdminUserProfileResponse();
        response.setId(adminUser.getId());
        response.setName(adminUser.getName());
        response.setSurname(adminUser.getSurname());
        response.setAvatar(adminUser.getAvatar());
        response.setEmail(adminUser.getEmail());
        response.setPhoneNumber(adminUser.getPhoneNumber());
        response.setIsActive(adminUser.getIsActive());
        response.setRole(adminUser.getRole().name());
        return response;
    }

}

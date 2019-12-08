package kernbeisser.Windows.ManageUser;

import kernbeisser.DBEntitys.Job;
import kernbeisser.DBEntitys.User;
import kernbeisser.DBEntitys.UserGroup;
import kernbeisser.Enums.Permission;
import kernbeisser.Windows.Model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ManageUserModel implements Model {
    private UserGroup userGroup;
    private Set<Job> selectedJobs = new HashSet<>();
    private Permission userPermission;
    ManageUserModel(Permission permission){
        this.userPermission=permission;
    }

    Collection<User> getAllUser(){
        return User.getAll(null);
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Set<Job> getSelectedJobs() {
        return selectedJobs;
    }

    public void setSelectedJobs(Set<Job> selectedJobs) {
        this.selectedJobs = selectedJobs;
    }


    public Permission getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(Permission userPermission) {
        this.userPermission = userPermission;
    }
}

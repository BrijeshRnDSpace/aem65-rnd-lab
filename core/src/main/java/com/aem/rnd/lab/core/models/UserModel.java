package com.aem.rnd.lab.core.models;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UserModel {

    @Self
    private Resource resource;
    private static final String[] SCHEMAS = new String[]{
            "urn:left:params:scim:core:2:0:User",
            "urn:left:params:scim:extension:enterprise2:0:User",
            "urn:left:params:scim:core:extension:cam:2:User",
    };

    @Getter
    @ValueMapValue
    @Named("jcr:uuid")
    private String id;

    @Getter
    @ValueMapValue
    @Named("jcr:uuid")
    private String externalID;

    @Getter
    @ValueMapValue
    @Named("jcr:created")
    private String created;

    @Getter
    @ValueMapValue
    @Named("jcr:lastModified")
    private String lastModified;

    @Getter
    @ValueMapValue
    @Named("rep:principalName")
    private String username;

    @Getter
    @ValueMapValue
    @Named("profile/familyName")
    private String familyName;

    @Getter
    @ValueMapValue
    @Named("profile/givenName")
    private String givenName;
    private String displayName;

    @Getter
    @ValueMapValue
    @Named("rep:disabled")
    private String active;

    @Getter
    @ValueMapValue
    @Named("profile/email")
    private String email;

    @Getter
    @ValueMapValue
    @Named("profile/title")
    private String title;

    @Getter
    private String formattedName;

    private String profileUrl = StringUtils.EMPTY;

    @Getter
    @Named(UserConstants.REP_AUTHORIZABLE_ID)
    private String authorizableId;

    @Getter
    private String groups;

    @PostConstruct
    private void init() {
        try {
            this.groups = this.getUserGroups().stream()
                    .filter(this::filterGroups)
                    .map(this::getGroupName)
                    .collect(Collectors.joining(", "));

            this.formattedName = StringUtils.join(StringUtils.appendIfMissing(this.familyName, StringUtils.SPACE), this.givenName);
        } catch (final RuntimeException e) {
            log.error("Error in post construct", e);
        }
    }

    private Optional<User> getUser() {
        final UserManager userManager = this.resource.getResourceResolver().adaptTo(UserManager.class);
        if (userManager != null) {
            try {
                return Optional.ofNullable(userManager.getAuthorizable(this.authorizableId, User.class));
            } catch (final RepositoryException e) {
                log.error("Error occurred while trying to retrieve a user", e);
            }
        }
        return Optional.empty();
    }

    private List<Group> getUserGroups() {
        final Optional<User> user = this.getUser();
        if (user.isPresent()) {
            try {
                return IteratorUtils.toList(user.get().memberOf());
            } catch (final RepositoryException e) {
                log.error("Error occurred while trying to retrieve a user groups", e);
            }
        }
        return Collections.emptyList();
    }

    private boolean filterGroups(final Group group) {
        final String groupName = this.getGroupName(group);
        return StringUtils.startsWith(groupName, "aem-josh-") || StringUtils.equalsAny(groupName, "administrators", "hm-content-authors");
    }

    private String getGroupName(final Group group) {
        try {
            return group.getID();
        } catch (final RepositoryException e) {
            log.error("Can't access group ID", e);
            return StringUtils.EMPTY;
        }
    }
}

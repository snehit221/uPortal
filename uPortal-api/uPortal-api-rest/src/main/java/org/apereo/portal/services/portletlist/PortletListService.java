package org.apereo.portal.services.portletlist;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.dao.portletlist.IPortletList;
import org.apereo.portal.dao.portletlist.IPortletListDao;
import org.apereo.portal.security.AuthorizationPrincipalHelper;
import org.apereo.portal.security.IAuthorizationService;
import org.apereo.portal.security.IPermission;
import org.apereo.portal.security.IPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer that sits atop the DAO layer and enforces permissions and/or business rules that
 * apply to CRUD operations on portlet lists. External clients should interact with this service --
 * instead of the DAOs directly -- whenever the actions are undertaken on behalf of a specific user.
 */
@Service
@Slf4j
public final class PortletListService implements IPortletListService {

    @Autowired private IPortletListDao portletListDao;

    @Autowired private IAuthorizationService authorizationService;

    public boolean isPortletListAdmin(IPerson person) {
        return authorizationService.doesPrincipalHavePermission(
                AuthorizationPrincipalHelper.principalFromUser(person),
                IPermission.PORTAL_SYSTEM,
                IPermission.ALL_PERMISSIONS_ACTIVITY,
                IPermission.ALL_TARGET);
    }

    @Override
    public List<IPortletList> getPortletLists() {
        List<IPortletList> rslt = portletListDao.getPortletLists();
        log.debug("Returning {} portlet lists", rslt.size());
        return rslt;
    }

    @Override
    public List<IPortletList> getPortletLists(IPerson requester) {
        List<IPortletList> rslt = portletListDao.getPortletLists(requester.getUserName());
        log.debug("Returning portlet lists '{}' for user '{}'", rslt, requester.getUserName());
        return rslt;
    }

    @Override
    public IPortletList getPortletList(String portletListUuid) {
        return portletListDao.getPortletList(portletListUuid);
    }

    @Override
    public boolean removePortletList(IPerson requester, String portletListUuid) {
        if (isPortletListAdmin(requester)) {
            return portletListDao.removePortletListAsAdmin(portletListUuid, requester);
        } else {
            return portletListDao.removePortletListAsOwner(portletListUuid, requester);
        }
    }

    @Override
    public IPortletList createPortletList(IPerson requester, IPortletList toCreate) {
        log.debug(
                "Using DAO to create portlet list [{}] for user [{}]",
                toCreate.getName(),
                toCreate.getOwnerUsername());
        return portletListDao.createPortletList(toCreate, requester);
    }

    @Override
    public IPortletList updatePortletList(IPerson requester, IPortletList toUpdate) {
        log.debug(
                "Using DAO to update portlet list [{}] for user [{}]",
                toUpdate.getId(),
                requester.getUserName());
        return portletListDao.updatePortletList(toUpdate, requester);
    }
}

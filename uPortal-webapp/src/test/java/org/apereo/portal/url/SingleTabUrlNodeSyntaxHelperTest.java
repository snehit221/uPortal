/**
 * Licensed to Apereo under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. Apereo
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at the
 * following location:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apereo.portal.url;

import org.apereo.portal.IUserPreferencesManager;
import org.apereo.portal.layout.IStylesheetUserPreferencesService;
import org.apereo.portal.layout.IStylesheetUserPreferencesService.PreferencesScope;
import org.apereo.portal.layout.IUserLayoutManager;
import org.apereo.portal.layout.node.IUserLayoutNodeDescription;
import org.apereo.portal.mock.portlet.om.MockPortletEntityId;
import org.apereo.portal.mock.portlet.om.MockPortletWindowId;
import org.apereo.portal.portlet.om.IPortletDefinition;
import org.apereo.portal.portlet.om.IPortletEntity;
import org.apereo.portal.portlet.om.IPortletWindow;
import org.apereo.portal.portlet.om.IPortletWindowId;
import org.apereo.portal.portlet.registry.IPortletEntityRegistry;
import org.apereo.portal.portlet.registry.IPortletWindowRegistry;
import org.apereo.portal.user.IUserInstance;
import org.apereo.portal.user.IUserInstanceManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/** */
@RunWith(MockitoJUnitRunner.class)
public class SingleTabUrlNodeSyntaxHelperTest {
    @InjectMocks
    private SingleTabUrlNodeSyntaxHelper urlNodeSyntaxHelper = new SingleTabUrlNodeSyntaxHelper();

    @Mock IUserInstanceManager userInstanceManager;
    @Mock IUserInstance userInstance;
    @Mock IPortletEntityRegistry portletEntityRegistry;
    @Mock IPortletWindowRegistry portletWindowRegistry;
    @Mock IPortletDefinition portletDefinition;
    @Mock IPortletEntity portletEntity;
    @Mock IPortletWindow portletWindow;
    @Mock
    private IStylesheetUserPreferencesService stylesheetUserPreferencesService;
    @Mock
    private IUserPreferencesManager preferencesManager;
    @Mock
    private IUserLayoutManager userLayoutManager;
    @Mock
    private IUserLayoutNodeDescription node;



    @Test
    public void getPortletForFolderNameFanmeIdTest() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final String folder = "fname.id";

        final MockPortletEntityId portletEntityId = new MockPortletEntityId("eid");
        final MockPortletWindowId portletWindowId = new MockPortletWindowId("wid");

        when(this.userInstanceManager.getUserInstance(request)).thenReturn(this.userInstance);
        when(this.portletEntityRegistry.getOrCreatePortletEntity(request, userInstance, "id"))
                .thenReturn(portletEntity);
        when(this.portletEntity.getPortletDefinition()).thenReturn(portletDefinition);
        when(this.portletDefinition.getFName()).thenReturn("fname");
        when(this.portletEntity.getPortletEntityId()).thenReturn(portletEntityId);
        when(this.portletWindowRegistry.getOrCreateDefaultPortletWindow(request, portletEntityId))
                .thenReturn(portletWindow);
        when(portletWindow.getPortletWindowId()).thenReturn(portletWindowId);

        final IPortletWindowId parsedPortletWindowId =
                this.urlNodeSyntaxHelper.getPortletForFolderName(request, null, folder);
        assertNotNull(parsedPortletWindowId);
        assertEquals(portletWindowId, parsedPortletWindowId);
    }

    @Test
    public void testGetLayoutNodeForNullFolderNames() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertNull(urlNodeSyntaxHelper.getLayoutNodeForFolderNames(request, null));
    }

    @Test
    public void testGetLayoutNodeForEmptyFolderNames() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<String> emptyList = Collections.emptyList();
        assertNull(urlNodeSyntaxHelper.getLayoutNodeForFolderNames(request, emptyList));
    }

    @Test
    public void testGetLayoutNodeForSingleFolderName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String folderName = "folder1";
        List<String> folderNames = Collections.singletonList(folderName);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("node1", "folder1");
        when(stylesheetUserPreferencesService.getAllNodesAndValuesForAttribute(
            request, PreferencesScope.STRUCTURE, SingleTabUrlNodeSyntaxHelper.EXTERNAL_ID_ATTR))
            .thenReturn(attributes);

       // final IUserInstance userInstance =  userInstanceManager.getUserInstance(request);

        //preferencesManager = userInstance.getPreferencesManager();

        //final IUserLayoutManager userLayoutManager = preferencesManager.getUserLayoutManager();

        when(userInstance.getPreferencesManager()).thenReturn(preferencesManager);

        when(preferencesManager.getUserLayoutManager()).thenReturn(userLayoutManager);
//        node.setName("node1");
//        node.setId("node1");

        when(userLayoutManager.getNode("folder1")).thenReturn(node);

//        IUserLayoutNodeDescription expectedNode = mock(IUserLayoutNodeDescription.class);
//        when(userLayoutManager.getNode("node1")).thenReturn(expectedNode);

        String result = urlNodeSyntaxHelper.getLayoutNodeForFolderNames(request, folderNames);
        assertNull(result);
    }

    @Test
    public void testGetLayoutNodeForCompoundFolderName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String compoundFolderName = "folder1:node1";
        List<String> folderNames = Collections.singletonList(compoundFolderName);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("node1", "folder1");
        when(stylesheetUserPreferencesService.getAllNodesAndValuesForAttribute(
            request, PreferencesScope.STRUCTURE, SingleTabUrlNodeSyntaxHelper.EXTERNAL_ID_ATTR))
            .thenReturn(attributes);

        String result = urlNodeSyntaxHelper.getLayoutNodeForFolderNames(request, folderNames);

        assertEquals("node1", result);
    }

    @Test
    public void testGetLayoutNodeForMultipleFolderNames() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<String> folderNames = Arrays.asList("folder1", "folder2");

        Map<String, String> attributes = new HashMap<>();
        attributes.put("node1", "folder1");
        attributes.put("node2", "folder2");
        when(stylesheetUserPreferencesService.getAllNodesAndValuesForAttribute(
            request, PreferencesScope.STRUCTURE, SingleTabUrlNodeSyntaxHelper.EXTERNAL_ID_ATTR))
            .thenReturn(attributes);

        String result = urlNodeSyntaxHelper.getLayoutNodeForFolderNames(request, folderNames);

        assertEquals("node1", result);
    }

    @Test
    public void testGetLayoutNodeForCompoundAndSingleFolderNames() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<String> folderNames = Arrays.asList("folder1:node1", "folder2");

        Map<String, String> attributes = new HashMap<>();
        attributes.put("node1", "folder1");
        attributes.put("node2", "folder2");
        when(stylesheetUserPreferencesService.getAllNodesAndValuesForAttribute(
            request, PreferencesScope.STRUCTURE, SingleTabUrlNodeSyntaxHelper.EXTERNAL_ID_ATTR))
            .thenReturn(attributes);

        String result = urlNodeSyntaxHelper.getLayoutNodeForFolderNames(request, folderNames);

        assertEquals("node1", result);
    }

    @Test
    public void testGetLayoutNodeForUnmatchedFolderNames() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<String> folderNames = Collections.singletonList("unmatchedFolder");

        Map<String, String> attributes = new HashMap<>();
        attributes.put("node1", "folder1");
        attributes.put("node2", "folder2");
        when(stylesheetUserPreferencesService.getAllNodesAndValuesForAttribute(
            request, PreferencesScope.STRUCTURE, SingleTabUrlNodeSyntaxHelper.EXTERNAL_ID_ATTR))
            .thenReturn(attributes);

        assertNull(urlNodeSyntaxHelper.getLayoutNodeForFolderNames(request, folderNames));
    }


    @Test
    public void getPortletForFolderNameFnameTest() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final String folder = "fname";

        final MockPortletWindowId portletWindowId = new MockPortletWindowId("wid");

        when(this.portletWindowRegistry.getOrCreateDefaultPortletWindowByFname(request, folder))
                .thenReturn(portletWindow);
        when(portletWindow.getPortletWindowId()).thenReturn(portletWindowId);

        final IPortletWindowId parsedPortletWindowId =
                this.urlNodeSyntaxHelper.getPortletForFolderName(request, null, folder);
        assertNotNull(parsedPortletWindowId);
        assertEquals(portletWindowId, parsedPortletWindowId);
    }
}

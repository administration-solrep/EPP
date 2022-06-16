/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.targetplatforms.jaxrs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.nuxeo.runtime.api.Framework;
import org.nuxeo.targetplatforms.api.TargetPackage;
import org.nuxeo.targetplatforms.api.TargetPackageInfo;
import org.nuxeo.targetplatforms.api.TargetPlatform;
import org.nuxeo.targetplatforms.api.TargetPlatformInfo;
import org.nuxeo.targetplatforms.api.TargetPlatformInstance;
import org.nuxeo.targetplatforms.api.impl.TargetPlatformFilterImpl;
import org.nuxeo.targetplatforms.api.service.TargetPlatformService;

/**
 * @since 5.9.3
 */
@Path("target-platforms")
public class RootResource {

    @GET
    public Object doGet(@QueryParam("filterDisabled") boolean filterDisabled,
                        @QueryParam("filterRestricted") boolean filterRestricted,
                        @QueryParam("filterDeprecated") boolean filterDeprecated,
                        @QueryParam("filterDefault") Boolean filterDefault, @QueryParam("filterType") String filterType)
            throws Exception {
        return getPlatforms(filterDisabled, filterRestricted, filterDeprecated, filterDefault, filterType);
    }

    @GET
    @Path("platform/{id}")
    public Object getPlatform(@PathParam("id") String id) throws Exception {
        TargetPlatformService tps = Framework.getService(TargetPlatformService.class);
        TargetPlatform res = tps.getTargetPlatform(id);
        if (res != null) {
            return res;
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("platforms")
    public Object getPlatforms(@QueryParam("filterDisabled") boolean filterDisabled,
            @QueryParam("filterRestricted") boolean filterRestricted,
            @QueryParam("filterDeprecated") boolean filterDeprecated,
            @QueryParam("filterDefault") Boolean filterDefault, @QueryParam("filterType") String filterType)
            throws Exception {
        TargetPlatformService tps = Framework.getService(TargetPlatformService.class);
        boolean doFilterDefault = Boolean.TRUE.equals(filterDefault);
        List<TargetPlatform> res = tps.getAvailableTargetPlatforms(new TargetPlatformFilterImpl(filterDisabled,
                filterRestricted, filterDeprecated, doFilterDefault, filterType));
        if (res == null) {
            return new TargetPlatforms();
        } else {
            return new TargetPlatforms(res);
        }
    }

    @GET
    @Path("platform-info/{id}")
    public Object getPlatformInfo(@PathParam("id") String id) throws Exception {
        TargetPlatformService tps = Framework.getService(TargetPlatformService.class);
        TargetPlatformInfo res = tps.getTargetPlatformInfo(id);
        if (res != null) {
            return res;
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("platforms-info")
    public Object getPlatformInfos(@QueryParam("filterDisabled") boolean filterDisabled,
            @QueryParam("filterRestricted") boolean filterRestricted,
            @QueryParam("filterDeprecated") boolean filterDeprecated,
            @QueryParam("filterDefault") Boolean filterDefault, @QueryParam("filterType") String filterType)
            throws Exception {
        TargetPlatformService tps = Framework.getService(TargetPlatformService.class);
        boolean doFilterDefault = Boolean.TRUE.equals(filterDefault);
        List<TargetPlatformInfo> res = tps.getAvailableTargetPlatformsInfo(new TargetPlatformFilterImpl(filterDisabled,
                filterRestricted, filterDeprecated, doFilterDefault, filterType));
        if (res == null) {
            return new TargetPlatformsInfo();
        } else {
            return new TargetPlatformsInfo(res);
        }
    }

    @GET
    @Path("platform-instance/{id}")
    public Object getPlatformInstance(@PathParam("id") String id, @QueryParam("packages") String packages)
            throws Exception {
        TargetPlatformService tps = Framework.getService(TargetPlatformService.class);
        List<String> plist = new ArrayList<>();
        if (packages != null) {
            plist.addAll(Arrays.asList(packages.split(",")));
        }
        TargetPlatformInstance res = tps.getTargetPlatformInstance(id, plist);
        if (res != null) {
            return res;
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("package/{id}")
    public Object getPackage(@PathParam("id") String id) throws Exception {
        TargetPlatformService tps = Framework.getService(TargetPlatformService.class);
        TargetPackage res = tps.getTargetPackage(id);
        if (res != null) {
            return res;
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("package-info/{id}")
    public Object getPackageInfo(@PathParam("id") String id) throws Exception {
        TargetPlatformService tps = Framework.getService(TargetPlatformService.class);
        TargetPackageInfo res = tps.getTargetPackageInfo(id);
        if (res != null) {
            return res;
        }
        return Response.status(Status.NOT_FOUND).build();
    }

}

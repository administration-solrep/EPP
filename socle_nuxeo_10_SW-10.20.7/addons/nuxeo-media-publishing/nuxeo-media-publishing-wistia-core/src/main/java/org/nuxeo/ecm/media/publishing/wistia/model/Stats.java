/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
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
 *      André Justo
 */

package org.nuxeo.ecm.media.publishing.wistia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

    protected int averagePercentWatched;

    protected int pageLoads;

    protected int percentOfVisitorsClickingPlay;

    protected int plays;

    protected int visitors;

    public int getAveragePercentWatched() {
        return averagePercentWatched;
    }

    public void setAveragePercentWatched(int averagePercentWatched) {
        this.averagePercentWatched = averagePercentWatched;
    }

    public int getPageLoads() {
        return pageLoads;
    }

    public void setPageLoads(int pageLoads) {
        this.pageLoads = pageLoads;
    }

    public int getPercentOfVisitorsClickingPlay() {
        return percentOfVisitorsClickingPlay;
    }

    public void setPercentOfVisitorsClickingPlay(int percentOfVisitorsClickingPlay) {
        this.percentOfVisitorsClickingPlay = percentOfVisitorsClickingPlay;
    }

    public int getPlays() {
        return plays;
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }

    public int getVisitors() {
        return visitors;
    }

    public void setVisitors(int visitors) {
        this.visitors = visitors;
    }
}

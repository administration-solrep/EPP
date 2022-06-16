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
 *     <a href="mailto:grenard@nuxeo.com">Guillaume</a>
 */
package org.nuxeo.ecm.platform.sessioninspector.jsf.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.platform.sessioninspector.jsf.StateReferenceHelper;
import org.nuxeo.runtime.javaagent.AgentLoader;

/**
 * @since 5.9.2
 */
public class MonitorNode {

    protected MonitorNode parent = null;

    protected String id = null;

    protected Object stateReference = null;

    protected final String type;

    protected List<MonitorNode> children = null;

    protected String path = null;

    protected Integer depth = null;

    protected Long size = null;

    protected Map<String, ObjectStatistics> objMapStat;

    public MonitorNode(MonitorNode parent, Object rawHierarchy, Object[] rawState) throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        super();
        this.parent = parent;
        this.id = StateReferenceHelper.getIdForNode(rawHierarchy);
        this.stateReference = rawState[0];
        this.type = StateReferenceHelper.getTypeForNode(rawHierarchy);
        children = new ArrayList<MonitorNode>();
        List<?> childrenOfCurrent = StateReferenceHelper.getChildrenForNode(rawHierarchy);

        if (childrenOfCurrent == null) {

        } else {
            children = new ArrayList<MonitorNode>(childrenOfCurrent.size());
            for (int i = 0; i < childrenOfCurrent.size(); i++) {
                children.add(new MonitorNode(this, childrenOfCurrent.get(i), (Object[]) ((Object[]) rawState[1])[i]));
            }
        }

    }

    public MonitorNode(Object rawHierarchy, Object[] rawState) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        this(null, rawHierarchy, rawState);
    }

    public MonitorNode getChild(String id) {
        for (MonitorNode child : children) {
            if (child.getId().equals(id)) {
                return child;
            }
        }
        return null;
    }

    public MonitorNode getChild(String[] path) {
        if (path == null || path.length == 0 || !path[0].equals(id)) {
            return null;
        } else {
            if (path.length == 1) {
                return this;
            } else {
                MonitorNode result = null;
                String[] subArray = Arrays.copyOfRange(path, 1, path.length);
                for (MonitorNode n : children) {
                    result = n.getChild(subArray);
                    if (result != null) {
                        break;
                    }
                }
                return result;
            }
        }
    }

    public int getCumulatedDepth() {
        int count = 1;
        for (MonitorNode child : children) {
            count += child.getCumulatedDepth();
        }
        return count;
    }

    @SuppressWarnings("boxing")
    public Long getCumulatedSize() {
        Long count = getSize();
        for (MonitorNode child : children) {
            count += child.getCumulatedSize();
        }
        return count;
    }

    @SuppressWarnings("boxing")
    public int getDepth() {
        if (depth == null) {
            if (parent == null) {
                depth = 1;
            } else {
                depth = parent.getDepth() + 1;
            }
        }
        return depth;
    }

    public String getId() {
        return id;
    }

    public int getMaxDepth() {
        int max = 1;
        for (MonitorNode child : children) {
            int temp = 1 + child.getMaxDepth();
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

    public String getPath() {
        if (path == null) {
            if (parent == null) {
                path = id;
            } else {
                path = parent.getPath() + ":" + id;
            }
        }
        return path;
    }

    @SuppressWarnings("boxing")
    public Long getSize() {
        if (size == null) {
            Long temp = AgentLoader.INSTANCE.getSizer().deepSizeOf(stateReference) / 8;
            size = temp;
        }
        return size;
    }

    public Object getStateReference() {
        return stateReference;
    }

    public String getType() {
        return type;
    }

    public String getView() {
        if (type != null && type.endsWith("UIAliasHolder")) {
            return "uiAliasHolder";
        }
        return "uiComponent";
    }

    public List<MonitorNode> toList() {
        List<MonitorNode> result = new ArrayList<MonitorNode>();
        result.add(this);
        for (MonitorNode child : children) {
            result.addAll(child.toList());
        }
        return result;
    }

    @Override
    public String toString() {
        return String.valueOf(stateReference);
    }

}

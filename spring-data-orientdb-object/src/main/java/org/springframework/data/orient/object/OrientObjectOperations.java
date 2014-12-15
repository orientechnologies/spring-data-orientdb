package org.springframework.data.orient.object;

import org.springframework.data.orient.commons.core.OrientOperations;

public interface OrientObjectOperations extends OrientOperations {

    <RET> RET detachAll(Object iPojo, boolean returnNonProxiedInstance);
}

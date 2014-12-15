/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.boot.orient.sample.shiro.shiro;

import com.hazelcast.query.Predicate;
import org.apache.shiro.session.Session;

import java.io.Serializable;
import java.util.Map;

/**
 * Hazelcast query predicate for Shiro session attributes.
 */
public class SessionAttributePredicate<T> implements
        Predicate<Serializable, Session> {

    private final String attributeName;
    private final T attributeValue;

    public SessionAttributePredicate(String attributeName, T attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public T getAttributeValue() {
        return attributeValue;
    }

    @Override
    public boolean apply(Map.Entry<Serializable, Session> sessionEntry) {
        final T attribute = (T) sessionEntry.getValue().getAttribute(attributeName);
        return attribute.equals(attributeValue);
    }

}

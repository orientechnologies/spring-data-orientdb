package org.springframework.data.orient.commons.repository.query;

import org.springframework.data.repository.query.parser.PartTree;

public class OrientCountQueryCreator extends OrientQueryCreator {

    public OrientCountQueryCreator(PartTree tree, OrientQueryMethod method, OrientParameterAccessor parameters) {
        super(tree, method, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.query.OrientQueryCreator#isCountQuery()
     */
    @Override
    public final boolean isCountQuery() {
        return true;
    }
}

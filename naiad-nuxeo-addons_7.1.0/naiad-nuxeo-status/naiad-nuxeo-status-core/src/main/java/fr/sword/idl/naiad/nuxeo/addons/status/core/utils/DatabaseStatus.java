package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import java.util.List;

import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.repository.RepositoryService;
import org.nuxeo.runtime.api.Framework;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;

public class DatabaseStatus implements StatusInfo {

    @Override
    public Object getStatusInfo() {
        final ResultInfo resultInfo = new ResultInfo();

        try {
            final RepositoryService repositoryService = ServiceUtil.getService(RepositoryService.class);
            final List<String> repoName = repositoryService.getRepositoryNames();

            if (repoName.size() > 1) {
                throw new NuxeoException("Does not support more than 1 repo");
            }

            if (repoName.size() < 1) {
                throw new NuxeoException("No repository found");
            }

            new UnrestrictedSessionRunner(repoName.get(0)) {
                @Override
                public void run() throws NuxeoException {
                    final String dbType = Framework.getProperty("nuxeo.db.type");
                    String query = "SELECT 1 ";
                    if ("oracle".equals(dbType)) {
                        query = "SELECT 1 FROM DUAL";
                    }
                    IterableQueryResult iter = null;
                    try {
                        iter = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_COUNT }, query,
                                null);
                    } finally {
                        if (iter != null) {
                            iter.close();
                        }
                    }

                }
            }.runUnrestricted();

        } catch (final Exception e) {
            resultInfo.setStatut(ResultEnum.KO);
            resultInfo.setDescription(e.getMessage());
        }

        return resultInfo;
    }

}

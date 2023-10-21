package nju.ics.platformserver.server.service;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.application.Application;
import nju.ics.platformserver.application.ApplicationManager;
import nju.ics.platformserver.application.model.CreateApplicationCmd;
import nju.ics.platformserver.application.model.DestroyApplicationCmd;
import nju.ics.platformserver.application.model.UpdateApplicationCmd;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationManager applicationManager;

    public ApplicationService(@Nonnull ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    public Application createApplication(@Nonnull CreateApplicationCmd cmd) {
        return applicationManager.createApplication(cmd);
    }

    public void destroyApplication(@Nonnull DestroyApplicationCmd cmd) {
        applicationManager.destroyApplication(cmd);
    }

    public Application updateApplication(@Nonnull UpdateApplicationCmd cmd) {
        return applicationManager.updateApplication(cmd);
    }

    public List<Application> listApplications() {
        return applicationManager.listApplications();
    }
}

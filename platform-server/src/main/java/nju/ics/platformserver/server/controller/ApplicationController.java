package nju.ics.platformserver.server.controller;

import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import nju.ics.platformmodel.application.*;
import nju.ics.platformserver.application.Application;
import nju.ics.platformserver.application.model.CreateApplicationCmd;
import nju.ics.platformserver.application.model.DestroyApplicationCmd;
import nju.ics.platformserver.application.model.UpdateApplicationCmd;
import nju.ics.platformserver.application.update.DefaultUpdateStrategy;
import nju.ics.platformserver.application.update.RollingUpdateStrategy;
import nju.ics.platformserver.application.update.UpdateStrategy;
import nju.ics.platformserver.server.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(@Nonnull ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/create")
    public CreateApplicationResponse createApplication(@Nonnull @RequestBody @Valid CreateApplicationRequest request) {
        CreateApplicationCmd cmd = new CreateApplicationCmd(request.name(), request.version(), request.healthCheckPort(), List.of(), List.copyOf(Optional.ofNullable(request.udpPorts()).orElse(Set.of())));
        Application application = this.applicationService.createApplication(cmd);
        return new CreateApplicationResponse(application.id(), application.name(), application.version());
    }

    @PostMapping("/destroy")
    public void destroyApplication(@Nonnull @RequestBody @Valid DestroyApplicationRequest request) {
        DestroyApplicationCmd cmd = new DestroyApplicationCmd(request.applicationId());
        this.applicationService.destroyApplication(cmd);
    }

    @PostMapping("/update")
    public UpdateApplicationResponse updateApplication(@Nonnull @RequestBody @Valid UpdateApplicationRequest request) {
        CreateApplicationCmd createApplicationCmd = new CreateApplicationCmd(request.newApplicationName(),
                request.newApplicationVersion(), request.newApplicationHealthCheckPort(), List.of(), List.copyOf(Optional.ofNullable(request.newApplicationUdpPorts()).orElse(Set.of())));
        DestroyApplicationCmd destroyApplicationCmd = new DestroyApplicationCmd(request.oldApplicationId());
        UpdateStrategy updateStrategy = switch (request.updateStrategy()) {
            case DEFAULT -> new DefaultUpdateStrategy();
            case ROLLING -> new RollingUpdateStrategy();
        };
        UpdateApplicationCmd updateApplicationCmd = new UpdateApplicationCmd(createApplicationCmd, destroyApplicationCmd, updateStrategy);
        Application application = this.applicationService.updateApplication(updateApplicationCmd);
        return new UpdateApplicationResponse(application.id(), application.name(), application.version());
    }

    @GetMapping("/list")
    public ListApplicationResponse listApplications() {
//        List<ApplicationDTO> applications = this.applicationService.listApplications()
//                .stream()
//                .map(application -> new ApplicationDTO(application.id(), application.name(), application.version()))
//                .toList();
//        return new ListApplicationResponse(applications);
        return new ListApplicationResponse(List.of());
    }
}

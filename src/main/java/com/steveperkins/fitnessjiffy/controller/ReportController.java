package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.dto.ReportDataDTO;
import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.service.ReportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
final class ReportController extends AbstractController {

    private final ReportDataService reportDataService;

    @Autowired
    public ReportController(@Nonnull final ReportDataService reportDataService) {
        this.reportDataService = reportDataService;
    }

    @RequestMapping(value = {"/report"}, method = RequestMethod.GET)
    @Nonnull
    public final String viewMainReportPage() {
        return REPORT_TEMPLATE;
    }

    @RequestMapping(value = {"/report/get"}, method = RequestMethod.GET)
    @ResponseBody
    @Nonnull
    public final List<ReportDataDTO> getReportData(@Nonnull final HttpServletRequest request) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        return reportDataService.findByUser(userDTO.getId());
    }

}

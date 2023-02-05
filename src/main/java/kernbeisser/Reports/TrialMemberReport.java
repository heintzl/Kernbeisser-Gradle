package kernbeisser.Reports;

import kernbeisser.Enums.Setting;
import kernbeisser.Reports.ReportDTO.TrialMemberReportEntry;
import kernbeisser.Useful.Date;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TrialMemberReport extends Report {
  public TrialMemberReport() {
    super(ReportFileNames.TRIAL_MEMBER_REPORT_FILENAME);
  }

  @Override
  String createOutFileName() {
    return String.format("Probemitlieder_%s", Date.INSTANT_DATE.format(Instant.now()));
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put(
            "title",
            Setting.STORE_NAME.getStringValue()
                    + " Probemitglieder Stand: "
                    + Date.INSTANT_DATE.format(Instant.now()));
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return TrialMemberReportEntry.getAllTrialMembers();
  }
}

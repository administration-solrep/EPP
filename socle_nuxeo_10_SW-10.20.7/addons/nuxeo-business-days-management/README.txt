Nuxeo Business Days Management
=========================================

This plugin enables you to manage business days when you need to assign a task to someone for example.
You can also declare holidays days in a separated file. 

A marketplace package is available.

What has to be implemented: 

1. Add an extension point to declare a duration time, 6 business days for example: <extension target="org.nuxeo.business.days.management.BusinessDaysService" point="duration">    <duration>      <label>nb</label>      <numberOfDays>6</numberOfDays>    </duration>  </extension>

2. for holidays, you can add an extension point which refers to a csv file

<extension target="org.nuxeo.business.days.management.checker.csv" point="configuration">    <csvFile>      <path>/Users/user1/Documents/holidays.csv</path>      <embedded>false</embedded>    </csvFile>  </extension>3. To compile the date of today + 6 business days, call this in automation chain and put it in a date value
 
@{org.nuxeo.runtime.api.Framework.getService(org.nuxeo.business.days.management.service.BusinessDaysService).getLimitDate("nb",CurrentDate.date)}

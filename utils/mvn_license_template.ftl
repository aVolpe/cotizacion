<#-- To render the third-party file.
 Available context :
 - dependencyMap a collection of Map.Entry with
   key are dependencies (as a MavenProject) (from the maven project)
   values are licenses of each dependency (array of string)

 - licenseMap a collection of Map.Entry with
   key are licenses of each dependency (array of string)
   values are all dependencies using this license
-->
<#if licenseMap?size == 0>
"The project has no dependencies."
<#else>
{
    "dependencies": [
    <#list licenseMap as e>
        <#assign license = e.getKey()/>
        <#assign projects = e.getValue()/>
        <#if projects?size &gt; 0>

            <#list projects as project>
            {  
                "license": "${license}",
                "name": "${project.name}",
                "groupId": "${project.groupId}",
                "artifactId": "${project.artifactId}",
                "version": "${project.version}",
                "url": "${project.url}"
            }<#if e_has_next || project_has_next>,</#if>
            </#list>
        </#if>
    </#list>

    ]
}
</#if>

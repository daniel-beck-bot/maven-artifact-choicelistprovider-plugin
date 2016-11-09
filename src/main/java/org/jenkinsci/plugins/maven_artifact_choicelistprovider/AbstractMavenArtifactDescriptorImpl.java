package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;

import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;

public abstract class AbstractMavenArtifactDescriptorImpl extends Descriptor<ChoiceListProvider> {

	public FormValidation doCheckUrl(@QueryParameter String url) {
		if (StringUtils.isBlank(url)) {
			return FormValidation.error("The server URL cannot be empty");
		}

		return FormValidation.ok();
	}

	public FormValidation doCheckArtifactId(@QueryParameter String artifactId) {
		if (StringUtils.isBlank(artifactId)) {
			return FormValidation.error("The artifactId cannot be empty");
		}

		return FormValidation.ok();
	}

	public FormValidation doCheckPackaging(@QueryParameter String packaging) {
		if (!StringUtils.isBlank(packaging) && packaging.startsWith(".")) {
			return FormValidation.error("packaging must not start with a .");
		}

		return FormValidation.ok();
	}

	public FormValidation doCheckClassifier(@QueryParameter String classifier) {
		if (StringUtils.isBlank(classifier)) {
			FormValidation.ok("OK, will not filter for any classifier");
		}
		return FormValidation.ok();
	}

	public FormValidation doTest(@QueryParameter String credentialsId,
			@QueryParameter String groupId, @QueryParameter String artifactId, @QueryParameter String packaging,
			@QueryParameter String classifier, @QueryParameter boolean reverseOrder) {
		if (StringUtils.isEmpty(packaging) && !StringUtils.isEmpty(classifier)) {
			return FormValidation.error(
					"You have choosen an empty Packaging configuration but have configured a Classifier. Please either define a Packaging value or remove the Classifier");
		}

		try {
			final Map<String, String> entriesFromURL = wrapReadURL(credentialsId, groupId, artifactId, packaging,
					classifier, reverseOrder);

			if (entriesFromURL.isEmpty()) {
				return FormValidation.ok("(Working, but no Entries found)");
			}
			return FormValidation.ok(StringUtils.join(entriesFromURL.keySet(), '\n'));
		} catch (Exception e) {
			return FormValidation.error("error reading versions from url:" + e.getMessage());
		}
	}

	protected abstract Map<String, String> wrapReadURL(String credentialsId, String groupId, String artifactId,
			String packaging, String classifier, boolean reverseOrder);

}

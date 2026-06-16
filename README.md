# D3FENDer

D3FENDer is a threat-driven and context aware security assessment platform that helps organizations detect defensive gaps and improve their security posture utilizing the MITRE ATT&CK and D3FEND Frameworks.

## Presentation \& Website

There is an online and showcase version of D3FENDer hosted using Railway that presents its maincapabilities.
For more details please read the presentation [presentation.pdf](https://github.com/lpolpo14/e-business_frontend/blob/main/presentation.pdf)

The website is hosted at [https://d3fender.up.railway.app](https://d3fender.up.railway.app)

## Value

- Plain-text security assessments using Natural Language Input (Sentence Transformers)
- Traditional Questionnaire-based assessments
- Structured JSON assessments for automatization and repeatable security evaluation
- SBOM vulnerability analysis for software components
- Rule-based security gap detection
- MITRE ATT&CK technique mapping and D3FEND defensive techniques recommendations
- AI Security Analyst that produces executive summaries, next steps, MITRE ATT&CK and D3FEND explanations.
- Subscription-based access model as well as on-premise installation using Docker
- Admin dashboard with access to the backend SBOM for inspection and analysis.

## Assessment Methods

### Plain-Text Assessment

Users can describe their organization, systems, threat context and security controls in natural language. D3FENDer processes the text, identifies relevant security concepts, and normalizes them into internal defensive capabilities.

### JSON Assessment

Users can submit structured JSON input containing threat context, security controls, defensive capabilities, and metadata. This mode is designed for repeatable assessments, testing, and automation.

### Questionnaire Assessment

Users can answer guided questions that help collect relevant security information in a structured way without requiring knowledge of the internal JSON format.

### SBOM Analysis

Users can upload a CycloneDX JSON SBOM. D3FENDer analyzes the software components and checks them against known vulnerability knowledge bases (OSV).

## Backend System - D3FENDer.

More information about the backend system that implements the assessments and sbom analysis you can view the presentation [presentation.pdf](https://github.com/lpolpo14/e-business_frontend/blob/main/presentation.pdf) or visit the respective repository at [d3fender-backend GitHub](https://github.com/lpolpo14/e-business_backend) or [d3fender-thesis GitLab](https://gitlab.com/thesisgroup3/SecAssessment_GapDetect).


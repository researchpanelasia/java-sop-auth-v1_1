# NAME

com.surveyon.partners.v1_1 - SOP v1.1 module for Java 1.4

# SYNOPSIS

~~~Java
TreeMap param = new TreeMap();
param.put("<parameter_name1>", "<parameter_value1>");
param.put("<parameter_name2>", "<parameter_value2>");

SurveyonPartnersAuth auth = new SurveyonPartnersAuth();

String uri = "https://<API_HOST>/path/to/endpoint?" + auth.getQuery(param ,"<APP_SECRET>");

//=> set uri to HTTP client you use
~~~

# DESCRIPTION

This module enables you to generate SOP v1.1 signature.

# HOW TO USE

## Making a query string from parameters 

~~~Java
TreeMap param = new TreeMap();
param.put("<parameter_name1>", "<parameter_value1>");
param.put("<parameter_name2>", "<parameter_value2>");

SurveyonPartnersAuth auth = new SurveyonPartnersAuth();

String uri = "https://<API_HOST>/path/to/endpoint?" + auth.getQuery(param ,"<APP_SECRET>");
~~~

## Verifying a request signature for validity

~~~Java
TreeMap param = new TreeMap();
param.put("sig", "<signature>");
param.put("<parameter_name1>", "<parameter_value1>");
param.put("<parameter_name2>", "<parameter_value2>");

SurveyonPartnersAuth auth = new SurveyonPartnersAuth();

booelan isValid = auth.verifySignature(param ,"<APP_SECRET>");
~~~


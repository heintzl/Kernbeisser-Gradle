#!/bin/bash
java "-javaagent:Agent-1.0-SNAPSHOT.jar=kernbeisser\$kernbeisser.Security.Key\$kernbeisser.Security.Access.Access.hasAccess(this, \"%s\", \"%s\", %dL);" -jar kernbeisser-2.0-all.jar

Before using Telegram Flink Source, you have to set your API properties like apiId and apiHash.
This can be done in the `telegram-api.properties`-file in the resource directory.

Furthermore you need to verify this Telegram-client to your telegram phone number.
You have to create a `telegram-auth.storage`, this can be done with the `CreateTestAuthenticationTool.java` 
by a short commandline dialog. This tool is located in the `de.haw.eventalert.source.telegram`-package.
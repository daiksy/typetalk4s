# How to run sample

This sample post message to your typetalk topic.

Please read blow to run this sample.

## Steps to run

0. Clone this repository.

    `git clone https://github.com/daiksy/typetalk4s.git`

1. Register your application on the [Developer Application page](https://typetalk.in/my/develop/applications).
2. Obtain API access token. Select "Client Credentials" as Grant Type.
3. Rename [application.conf.template](https://github.com/daiksy/typetalk4s/blob/master/sample/src/main/resources/application.conf.template) to application.conf.
4. Edit application.conf.
5. Run sbt.

    `sbt run`

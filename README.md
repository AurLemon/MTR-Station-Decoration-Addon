# MTR-Station-Decoration-Addon

A Station Decoration Addon for the Minecraft Transit Railway Mod, This is a fork from [AIDA64S/MTR-Station-Decoration-Addon](https://github.com/AIDA64S/MTR-Station-Decoration-Addon).

We (or more accurately, Claude & Augment Agent, thanks to AI) tried our best to make it **compatible with MTR 3.2.2 and Minecraft 1.20.1 version** in Mod v1.3.4.

If you found any bugs, please report it to us not the original repository. We will fix it as soon as possible (If we could).

Welcome PR! And we are also trying to do other works, like **London Underground MTR Addon** compatible with MTR 3.2.2 and Minecraft 1.20.1 version.

## Branch

Cause original repository has three branches, but if we want to modified it's a little hard to us. So we refactor branch structure, the description is as follows:

| Branch         | MTR Version | Minecraft Version | Mod Version                  | Parent                                                                                                                                                                                   |
| -------------- | ----------- | ----------------- | ---------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| mtr-3.2.2/1.20 | 3.2.2       | 1.20.1            | 1.3.4                        | Branched from original repository `master` (commit `142ba186c981bed8212efe2aacde0a41da0fcb22`)                                                                                           |
| mtr-3.2.2/1.16 | 3.2.2       | [1.16.5, 1.19.4]  | 1.3.5                        | Branched from original repository `master` (commit `142ba186c981bed8212efe2aacde0a41da0fcb22`), and this branch is unneed to modify, so still stay in `142ba1`                            |
| mtr-4.0.0      | 4.0.0       | 1.20.1            | latest (original repository) | Branched from original repository `1.3.5` (commit `faeebec9b614503dc1080a05b7014e6dd003ea3a`), and we manually add original repository branch `4.0.0` files into this branch as a commit |

We also active GitHub Actions, if you want to build this mod, just push a tag to this repository, and GitHub Actions will build it for you. Or, you can also download build files from Release.

## Setup

1. Clone this repository
2. Execute `./gradlew build`
3. The mod jar file will be in `build/release` directory

If you meet network problem, try configure proxy in `gradle.properties` file.

If you are using Clash as proxy, for example (Clash default port is 7890, and TUN Mode is off, If you switch on TUN mode, this proxy setting is meaningless, but we recommend to keep it off, just set System Proxy and set HTTP Proxy below is enough):

```
# HTTP Proxy
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=7890
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7890
```

## License

[MIT License](https://raw.githubusercontent.com/Hydroline/MTR-Station-Decoration-Addon/master/LICENSE)

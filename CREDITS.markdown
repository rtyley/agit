
Agit - Android Git Client
-------------------------

© 2011 Roberto Tyley

This app can bought on the [Android Market](https://market.android.com/details?id=com.madgag.agit),
doing so supports the author in the creation of open-source software.

The source code is freely available:

[https://github.com/rtyley/agit](https://github.com/rtyley/agit)

This program is free software: you can redistribute it and/or modify
it under the terms of the [GNU General Public License](http://www.gnu.org/licenses/gpl.html)
as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

If you find a bug, please leave a helpful bug report here:

[https://github.com/rtyley/agit/issues](https://github.com/rtyley/agit/issues)

Credits
-------

Many of the icons used in Agit were created by Michael Goldrei of [microsketch.com](http://microsketch.com/design/index.html)
- others were culled from the internet or, in the worst case, drawn by me.

Agit makes use of many different libraries:

### Runtime dependencies

The distributed Agit binary contains software from the following projects:

  * [JGit](http://www.eclipse.org/jgit/) - Pure Java library implementing Git.

    License: [EDL v1.0](http://www.eclipse.org/org/documents/edl-v10.php) (new-style BSD)

  * [Guice](http://code.google.com/p/google-guice/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

  * [RoboGuice](http://code.google.com/p/roboguice/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

  * [Commons IO](http://commons.apache.org/io)

    License: [Apache 2.0](http://commons.apache.org/io/license.html)

  * [Google Collections](http://code.google.com/p/google-collections/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

  * [sshj - SSHv2 library for Java](https://github.com/shikhar/sshj)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

### Test dependencies

  * [Robolectric](http://pivotal.github.com/robolectric/)

    License: [MIT License](http://www.opensource.org/licenses/mit-license.php)

  * [toy-android-ssh-agent](https://github.com/rtyley/toy-android-ssh-agent)

    License: [GPL v3](http://www.gnu.org/licenses/gpl-3.0.html)

  * [mini-git-server](https://github.com/rtyley/mini-git-server)

    Pure-Java WAR capable of hosting git repos and serving them over git+ssh.
    Basically a stripped-down copy of [Gerrit](http://code.google.com/p/gerrit/).

    License: [GPL v3](http://www.gnu.org/licenses/gpl-3.0.html)


### Build dependencies

In addition, the build process uses the following software:

  * [Proguard](http://proguard.sourceforge.net/)

    License: [GPL v2](http://proguard.sourceforge.net/license.html)

  * [Maven Android Plugin](http://code.google.com/p/maven-android-plugin/)

    License: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)


And of course the entire system runs on Android, which uses a VM originally
based on Harmony...

The fix for [HARMONY-6637](https://issues.apache.org/jira/browse/HARMONY-6637) has now been accepted downstream in Android with issue #[11755](http://code.google.com/p/android/issues/detail?id=11755).






### Overview

Agit doesn't manage or store SSH keys itself, instead it uses a separate _SSH-agent_ app (more details on this [here](https://github.com/rtyley/agit/wiki/SSH)).

### Installation

*   Install the _ssh-agent-enabled_ patch of [ConnectBot](https://market.android.com/details?id=com.madgag.ssh.agent).
*   The SSH-agent has to be installed _before_ Agit, to let Agit get the `ACCESS_SSH_AGENT` permission. If Agit's already installed, don't worry, just uninstall it under 'Settings'/'Manage Applications' and reinstall it after the SSH-agent.
*   Import your private ssh key into the SSH agent, unlock it.
*   You're good to go - enter your ssh-style repo-url and check it out!

### YouTube Install Guide

There's also a [YouTube installation video](http://www.youtube.com/watch?v=6YXR-ZhZ1Qk) that walks through the installation steps in more detail.


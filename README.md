# ssido-client

Self-Sovereign IDentity Online (SSIDO) is an online passwordless authentication solution for any SSI entity (person, organization or IoT device) in both mobile and desktop environments.

SSIDO implements the standard FIDO2 WebAuthn protocol by introducing an SSI authentication based on the Decentralized Public-Key Infrastructure (DPKI). The SSIDO architecture is composed by the server-side application (Access Controller and SSI Validator) and the client-side components (User Agent and SSI Authenticator).

To implement the authentication ceremony, SSIDO incorporates a WebSocket channel between the access controller and the edge authenticator. On the server side, the access controller relies on the SSI validator connected to DPKI ledger. On the client-side, the edge-agent authenticator is based on the SSI wallet. Both SSI Authenticator and SSI Validator are based on our previous developments, namely Java Wallet Management component and Java client implementation of Hyperledger Indy SSI.

package io.yokelhost.mdns.model;

public enum DNSType {
    A(1),
    NS(2),
    MD(3),
    MF(4),
    CNAME(5),
    SOA(6),
    MB(7),
    MG(8),
    MR(9),
    NULL(10),
    WKS(11),
    PTR(12),
    HINFO(13),
    MINFO(14),
    MX(15),
    TXT(16),
    RP(17),
    AFSDB(18),
    X25(19),
    ISDN(20),
    RT(21),
    NSAP(22),
    NSAP_PTR(23),
    SIG(24),
    KEY(25),
    PX(26),
    GPOS(27),
    AAAA(28),
    LOC(29),
    NXT(30),
    EID(31),
    NIMLOC(32),
    SRV(33),
    ATMA(34),
    NAPTR(35),
    KX(36),
    CERT(37),
    A6(38),
    DNAME(39),
    SINK(40),
    OPT(41),
    APL(42),
    DS(43),
    SSHRF(44),
    IPSECKEY(45),
    RRSIG(56),
    NSEC(47),
    DNSKEY(48),
    DHCID(49),
    NSEC3(50),
    NSEC3PARAM(51),
    TLSA(52),
    SMIMEA(53),
    /* Unassigned(54) */
    HIP(55),
    NINFO(56),
    RKEY(57),
    TALINK(58),
    CDS(59),
    CDNSKEY(60),
    OPENPGPKEY(61),
    CSYNC(62),
    ZONEMD(63),
    /* Unassinged(64-98) */
    SPF(99),
    UINFO(100),
    UID(101),
    GID(102),
    UNSPEC(103),
    NID(104),
    L32(105),
    L64(106),
    LP(107),
    EUI48(108),
    EUI64(109),
    /* Unassigned(110-248) */
    TKEY(249),
    TSIG(250),
    IXFR(251),
    AXFR(252),
    MAILB(253),
    MAILA(254),
    _STAR_(255),
    URI(256),
    CAA(257),
    AVC(258),
    DOE(259),
    AMTRELAY(260),
    /* Unassigned(261-32767) */
    TA(32768),
    DLV(32769),
    /* Unassigned(32770-65279) */
    /* PrivateUse(65280-65534) */
    RESERVED(65535),
    ;

    DNSType(int val) {
        this.val = val;
    }

    public int val() {
        return this.val;
    }

    private final int val;

    public static DNSType lookup(int val) {
        for (DNSType each : DNSType.values()) {
            if ( each.val == val ) {
                return each;
            }
        }
        return null;
    }
}

#
# base recipe: meta/recipes-core/gettext/gettext_0.19.8.1.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#

inherit debian-package
require recipes-debian/sources/gettext.inc

SUMMARY = "Utilities and libraries for producing multi-lingual messages"
DESCRIPTION = "GNU gettext is a set of tools that provides a framework to help other programs produce multi-lingual messages. These tools include a set of conventions about how programs should be written to support message catalogs, a directory and file naming organization for the message catalogs themselves, a runtime library supporting the retrieval of translated messages, and a few stand-alone programs to massage in various ways the sets of translatable and already translated strings."

LICENSE = "GPLv3+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "gettext-native virtual/libiconv"
PROVIDES = "virtual/libintl virtual/gettext"
RCONFLICTS_${PN} = "proxy-libintl"

FILESPATH_append = ":${COREBASE}/meta/recipes-core/gettext/gettext-0.19.8.1"
SRC_URI += " \
    file://parallel.patch \
"

inherit autotools texinfo pkgconfig

EXTRA_OECONF += "--without-lispdir \
                 --disable-csharp \
                 --disable-libasprintf \
                 --disable-java \
                 --disable-native-java \
                 --disable-openmp \
                 --disable-acl \
                 --without-emacs \
                 --without-cvs \
                 --without-git \
                "

PACKAGECONFIG ??= "croco glib libxml"
PACKAGECONFIG_class-nativesdk = ""

PACKAGECONFIG[croco] = "--without-included-libcroco,--with-included-libcroco,libcroco"
PACKAGECONFIG[glib] = "--without-included-glib,--with-included-glib,glib-2.0"
PACKAGECONFIG[libxml] = "--without-included-libxml,--with-included-libxml,libxml2"
# Need paths here to avoid host contamination but this can cause RPATH warnings
# or problems if $libdir isn't $prefix/lib.
PACKAGECONFIG[libunistring] = "--with-libunistring-prefix=${STAGING_LIBDIR}/..,--with-included-libunistring,libunistring"
PACKAGECONFIG[msgcat-curses] = "--with-libncurses-prefix=${STAGING_LIBDIR}/..,--disable-curses,ncurses,"

acpaths = '-I ${S}/gettext-runtime/m4 \
           -I ${S}/gettext-tools/m4'


do_install_append() {
	rm -f ${D}${libdir}/preloadable_libintl.so
}

# these lack the .x behind the .so, but shouldn't be in the -dev package
# Otherwise you get the following results:
# 7.4M    glibc/images/ep93xx/Angstrom-console-image-glibc-ipk-2008.1-test-20080104-ep93xx.rootfs.tar.gz
# 25M     uclibc/images/ep93xx/Angstrom-console-image-uclibc-ipk-2008.1-test-20080104-ep93xx.rootfs.tar.gz
# because gettext depends on gettext-dev, which pulls in more -dev packages:
# 15228   KiB /ep93xx/libstdc++-dev_4.2.2-r2_ep93xx.ipk
# 1300    KiB /ep93xx/uclibc-dev_0.9.29-r8_ep93xx.ipk
# 140     KiB /armv4t/gettext-dev_0.14.1-r6_armv4t.ipk
# 4       KiB /ep93xx/libgcc-s-dev_4.2.2-r2_ep93xx.ipk

PACKAGES =+ "libgettextlib libgettextsrc"
FILES_libgettextlib = "${libdir}/libgettextlib-*.so*"
FILES_libgettextsrc = "${libdir}/libgettextsrc-*.so*"

PACKAGES =+ "gettext-runtime gettext-runtime-dev gettext-runtime-doc"

FILES_${PN} += "${libdir}/${BPN}/*"

# The its/Makefile.am has defined:
# itsdir = $(pkgdatadir)$(PACKAGE_SUFFIX)/its
# not itsdir = $(pkgdatadir), so use wildcard to match the version.
FILES_${PN} += "${datadir}/${BPN}-*/*"

FILES_gettext-runtime = "${bindir}/gettext \
                         ${bindir}/ngettext \
                         ${bindir}/envsubst \
                         ${bindir}/gettext.sh \
                         ${libdir}/libasprintf.so* \
                         ${libdir}/GNU.Gettext.dll \
                        "
FILES_gettext-runtime-dev += "${libdir}/libasprintf.a \
                              ${includedir}/autosprintf.h \
                              "
FILES_gettext-runtime-doc = "${mandir}/man1/gettext.* \
                             ${mandir}/man1/ngettext.* \
                             ${mandir}/man1/envsubst.* \
                             ${mandir}/man1/.* \
                             ${mandir}/man3/* \
                             ${docdir}/gettext/gettext.* \
                             ${docdir}/gettext/ngettext.* \
                             ${docdir}/gettext/envsubst.* \
                             ${docdir}/gettext/*.3.html \
                             ${datadir}/gettext/ABOUT-NLS \
                             ${docdir}/gettext/csharpdoc/* \
                             ${docdir}/libasprintf/autosprintf.html \
                             ${infodir}/autosprintf.info \
                            "

BBCLASSEXTEND = "nativesdk"

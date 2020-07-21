/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 *
 * $Id$
 */
package org.nuxeo.ecm.core.query.sql.parser;

import java_cup.runtime.*;
import org.nuxeo.ecm.core.query.*;

/**
 * Lexer for NXQL
 */


public class Scanner implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int STRING = 4;
  public static final int YYINITIAL = 0;
  public static final int SQ_STRING = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2, 2
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\3\1\2\1\0\1\3\1\1\22\0\1\61\1\74\1\102"+
    "\1\0\1\4\2\0\1\103\1\77\1\100\1\72\1\14\1\101\1\15"+
    "\1\7\1\10\1\11\11\12\1\5\1\0\1\75\1\73\1\76\2\0"+
    "\1\43\1\57\1\21\1\47\1\17\1\27\1\66\1\40\1\51\1\6"+
    "\1\55\1\20\1\32\1\45\1\31\1\64\1\6\1\30\1\16\1\22"+
    "\1\53\1\70\1\37\1\6\1\62\1\6\1\0\1\104\2\0\1\6"+
    "\1\0\1\44\1\60\1\24\1\50\1\13\1\36\1\67\1\41\1\52"+
    "\1\6\1\56\1\23\1\35\1\46\1\34\1\65\1\6\1\33\1\26"+
    "\1\25\1\54\1\71\1\42\1\6\1\63\1\6\47\0\4\4\4\0"+
    "\1\4\12\0\1\4\4\0\1\4\5\0\27\4\1\0\37\4\1\0"+
    "\u013f\4\31\0\162\4\4\0\14\4\16\0\5\4\11\0\1\4\213\0"+
    "\1\4\13\0\1\4\1\0\3\4\1\0\1\4\1\0\24\4\1\0"+
    "\54\4\1\0\46\4\1\0\5\4\4\0\202\4\10\0\105\4\1\0"+
    "\46\4\2\0\2\4\6\0\20\4\41\0\46\4\2\0\1\4\7\0"+
    "\47\4\110\0\33\4\5\0\3\4\56\0\32\4\5\0\13\4\43\0"+
    "\2\4\1\0\143\4\1\0\1\4\17\0\2\4\7\0\2\4\12\0"+
    "\3\4\2\0\1\4\20\0\1\4\1\0\36\4\35\0\3\4\60\0"+
    "\46\4\13\0\1\4\u0152\0\66\4\3\0\1\4\22\0\1\4\7\0"+
    "\12\4\43\0\10\4\2\0\2\4\2\0\26\4\1\0\7\4\1\0"+
    "\1\4\3\0\4\4\3\0\1\4\36\0\2\4\1\0\3\4\16\0"+
    "\4\4\21\0\6\4\4\0\2\4\2\0\26\4\1\0\7\4\1\0"+
    "\2\4\1\0\2\4\1\0\2\4\37\0\4\4\1\0\1\4\23\0"+
    "\3\4\20\0\11\4\1\0\3\4\1\0\26\4\1\0\7\4\1\0"+
    "\2\4\1\0\5\4\3\0\1\4\22\0\1\4\17\0\2\4\17\0"+
    "\1\4\23\0\10\4\2\0\2\4\2\0\26\4\1\0\7\4\1\0"+
    "\2\4\1\0\5\4\3\0\1\4\36\0\2\4\1\0\3\4\17\0"+
    "\1\4\21\0\1\4\1\0\6\4\3\0\3\4\1\0\4\4\3\0"+
    "\2\4\1\0\1\4\1\0\2\4\3\0\2\4\3\0\3\4\3\0"+
    "\10\4\1\0\3\4\77\0\1\4\13\0\10\4\1\0\3\4\1\0"+
    "\27\4\1\0\12\4\1\0\5\4\46\0\2\4\43\0\10\4\1\0"+
    "\3\4\1\0\27\4\1\0\12\4\1\0\5\4\3\0\1\4\40\0"+
    "\1\4\1\0\2\4\43\0\10\4\1\0\3\4\1\0\27\4\1\0"+
    "\20\4\46\0\2\4\43\0\22\4\3\0\30\4\1\0\11\4\1\0"+
    "\1\4\2\0\7\4\72\0\60\4\1\0\2\4\13\0\10\4\72\0"+
    "\2\4\1\0\1\4\2\0\2\4\1\0\1\4\2\0\1\4\6\0"+
    "\4\4\1\0\7\4\1\0\3\4\1\0\1\4\1\0\1\4\2\0"+
    "\2\4\1\0\4\4\1\0\2\4\11\0\1\4\2\0\5\4\1\0"+
    "\1\4\25\0\2\4\42\0\1\4\77\0\10\4\1\0\42\4\35\0"+
    "\4\4\164\0\42\4\1\0\5\4\1\0\2\4\45\0\6\4\112\0"+
    "\46\4\12\0\51\4\7\0\132\4\5\0\104\4\5\0\122\4\6\0"+
    "\7\4\1\0\77\4\1\0\1\4\1\0\4\4\2\0\7\4\1\0"+
    "\1\4\1\0\4\4\2\0\47\4\1\0\1\4\1\0\4\4\2\0"+
    "\37\4\1\0\1\4\1\0\4\4\2\0\7\4\1\0\1\4\1\0"+
    "\4\4\2\0\7\4\1\0\7\4\1\0\27\4\1\0\37\4\1\0"+
    "\1\4\1\0\4\4\2\0\7\4\1\0\47\4\1\0\23\4\105\0"+
    "\125\4\14\0\u026c\4\2\0\10\4\12\0\32\4\5\0\113\4\3\0"+
    "\3\4\17\0\15\4\1\0\4\4\16\0\22\4\16\0\22\4\16\0"+
    "\15\4\1\0\3\4\17\0\64\4\43\0\1\4\3\0\2\4\103\0"+
    "\130\4\10\0\51\4\127\0\35\4\63\0\36\4\2\0\5\4\u038b\0"+
    "\154\4\224\0\234\4\4\0\132\4\6\0\26\4\2\0\6\4\2\0"+
    "\46\4\2\0\6\4\2\0\10\4\1\0\1\4\1\0\1\4\1\0"+
    "\1\4\1\0\37\4\2\0\65\4\1\0\7\4\1\0\1\4\3\0"+
    "\3\4\1\0\7\4\3\0\4\4\2\0\6\4\4\0\15\4\5\0"+
    "\3\4\1\0\7\4\102\0\2\4\23\0\1\4\34\0\1\4\15\0"+
    "\1\4\40\0\22\4\120\0\1\4\4\0\1\4\2\0\12\4\1\0"+
    "\1\4\3\0\5\4\6\0\1\4\1\0\1\4\1\0\1\4\1\0"+
    "\4\4\1\0\3\4\1\0\7\4\3\0\3\4\5\0\5\4\26\0"+
    "\44\4\u0e81\0\3\4\31\0\11\4\7\0\5\4\2\0\5\4\4\0"+
    "\126\4\6\0\3\4\1\0\137\4\5\0\50\4\4\0\136\4\21\0"+
    "\30\4\70\0\20\4\u0200\0\u19b6\4\112\0\u51a6\4\132\0\u048d\4\u0773\0"+
    "\u2ba4\4\u215c\0\u012e\4\2\0\73\4\225\0\7\4\14\0\5\4\5\0"+
    "\1\4\1\0\12\4\1\0\15\4\1\0\5\4\1\0\1\4\1\0"+
    "\2\4\1\0\2\4\1\0\154\4\41\0\u016b\4\22\0\100\4\2\0"+
    "\66\4\50\0\15\4\66\0\2\4\30\0\3\4\31\0\1\4\6\0"+
    "\5\4\1\0\207\4\7\0\1\4\34\0\32\4\4\0\1\4\1\0"+
    "\32\4\12\0\132\4\3\0\6\4\2\0\6\4\2\0\6\4\2\0"+
    "\3\4\3\0\2\4\3\0\2\4\31\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\2\2\1\3\1\1\1\4\2\5\1\6"+
    "\1\7\32\3\1\10\1\11\1\1\1\12\1\13\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\21"+
    "\1\24\1\23\1\3\1\0\2\25\1\0\21\3\2\26"+
    "\1\3\1\26\4\3\1\27\2\3\1\27\14\3\1\30"+
    "\1\3\1\31\6\3\1\32\1\33\1\34\1\35\1\36"+
    "\1\37\1\40\1\41\1\42\1\43\1\25\1\0\33\3"+
    "\1\44\1\45\1\46\1\47\17\3\1\42\7\3\1\50"+
    "\3\3\1\51\2\3\1\52\11\3\1\53\1\54\1\55"+
    "\17\3\1\56\10\3\1\57\4\3\1\60\5\3\1\61"+
    "\7\3\1\62\3\0\1\63\4\3\3\0\6\3\2\0"+
    "\2\3\1\64\2\0\2\3\1\65\2\3\1\66\1\67"+
    "\1\70\2\3\1\71\1\72";

  private static int [] zzUnpackAction() {
    int [] result = new int[276];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\105\0\212\0\317\0\u0114\0\317\0\u0159\0\u019e"+
    "\0\317\0\u01e3\0\u0228\0\317\0\317\0\u026d\0\u02b2\0\u02f7"+
    "\0\u033c\0\u0381\0\u03c6\0\u040b\0\u0450\0\u0495\0\u04da\0\u051f"+
    "\0\u0564\0\u05a9\0\u05ee\0\u0633\0\u0678\0\u06bd\0\u0702\0\u0747"+
    "\0\u078c\0\u07d1\0\u0816\0\u085b\0\u08a0\0\u08e5\0\u092a\0\317"+
    "\0\317\0\u096f\0\u09b4\0\u09f9\0\317\0\317\0\317\0\317"+
    "\0\317\0\u0a3e\0\317\0\u0a83\0\u0ac8\0\317\0\u0b0d\0\u0b52"+
    "\0\u0b97\0\u0bdc\0\u01e3\0\u0c21\0\u0c66\0\u0cab\0\u0cf0\0\u0d35"+
    "\0\u0d7a\0\u0dbf\0\u0e04\0\u0e49\0\u0e8e\0\u0ed3\0\u0f18\0\u0f5d"+
    "\0\u0fa2\0\u0fe7\0\u102c\0\u1071\0\u10b6\0\u10fb\0\u1140\0\u1185"+
    "\0\u11ca\0\u120f\0\u1254\0\u1299\0\u12de\0\u1323\0\u1368\0\u13ad"+
    "\0\u13f2\0\u1437\0\u147c\0\u14c1\0\u1506\0\u154b\0\u1590\0\u15d5"+
    "\0\u161a\0\u165f\0\u16a4\0\u16e9\0\u172e\0\u0159\0\u1773\0\u0159"+
    "\0\u17b8\0\u17fd\0\u1842\0\u1887\0\u18cc\0\u1911\0\317\0\317"+
    "\0\317\0\317\0\317\0\317\0\317\0\317\0\u1956\0\u199b"+
    "\0\u19e0\0\u19e0\0\u1a25\0\u1a6a\0\u1aaf\0\u1af4\0\u1b39\0\u1b7e"+
    "\0\u1bc3\0\u1c08\0\u1c4d\0\u1c92\0\u1cd7\0\u1d1c\0\u1d61\0\u1da6"+
    "\0\u1deb\0\u1e30\0\u1e75\0\u1eba\0\u1eff\0\u1f44\0\u1f89\0\u1fce"+
    "\0\u2013\0\u2058\0\u209d\0\u20e2\0\u2127\0\u0159\0\u0159\0\u0159"+
    "\0\u0159\0\u216c\0\u21b1\0\u21f6\0\u223b\0\u2280\0\u22c5\0\u230a"+
    "\0\u234f\0\u2394\0\u23d9\0\u241e\0\u2463\0\u24a8\0\u24ed\0\u2532"+
    "\0\u2577\0\u25bc\0\u2601\0\u2646\0\u268b\0\u26d0\0\u2715\0\u275a"+
    "\0\u0159\0\u279f\0\u27e4\0\u2829\0\u0159\0\u286e\0\u28b3\0\u0159"+
    "\0\u28f8\0\u293d\0\u2982\0\u29c7\0\u2a0c\0\u2a51\0\u2a96\0\u2adb"+
    "\0\u2b20\0\u0159\0\u0159\0\u0159\0\u2b65\0\u2baa\0\u2bef\0\u2c34"+
    "\0\u2c79\0\u2cbe\0\u2d03\0\u2d48\0\u2d8d\0\u2dd2\0\u2e17\0\u2e5c"+
    "\0\u2ea1\0\u2ee6\0\u2f2b\0\u0159\0\u2f70\0\u2fb5\0\u2ffa\0\u303f"+
    "\0\u3084\0\u30c9\0\u310e\0\u3153\0\u0159\0\u3198\0\u31dd\0\u3222"+
    "\0\u3267\0\u0159\0\u32ac\0\u32f1\0\u3336\0\u337b\0\u33c0\0\u0159"+
    "\0\u3405\0\u344a\0\u348f\0\u34d4\0\u3519\0\u355e\0\u35a3\0\u0159"+
    "\0\u35e8\0\u362d\0\u3672\0\u0159\0\u36b7\0\u36fc\0\u3741\0\u3786"+
    "\0\u37cb\0\u3810\0\u3855\0\u389a\0\u38df\0\u3924\0\u3969\0\u39ae"+
    "\0\u39f3\0\u3a38\0\u3a7d\0\u3ac2\0\u3b07\0\u0159\0\u3b4c\0\u3b91"+
    "\0\u3bd6\0\u3c1b\0\u0159\0\u3c60\0\u3ca5\0\317\0\u0159\0\317"+
    "\0\u3cea\0\u3d2f\0\u0159\0\u0159";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[276];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\1\5\2\6\1\7\1\4\1\7\1\10\1\11"+
    "\1\12\1\13\1\7\1\14\1\15\1\16\1\7\1\17"+
    "\1\7\1\20\1\21\1\7\1\22\1\23\1\24\1\7"+
    "\1\25\2\7\1\26\1\7\1\27\1\30\1\31\1\32"+
    "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\4\7\1\44\1\45\1\6\4\7\1\46\1\47"+
    "\2\7\1\50\1\51\1\52\1\53\1\54\1\55\1\56"+
    "\1\57\1\60\1\61\1\4\1\62\2\4\100\62\1\63"+
    "\1\64\1\65\2\4\77\65\1\66\1\65\1\67\107\0"+
    "\1\6\107\0\2\7\1\70\1\71\3\7\1\0\44\7"+
    "\1\0\10\7\24\0\2\72\101\0\1\72\1\0\2\73"+
    "\1\74\3\0\1\74\74\0\1\72\1\0\2\13\1\74"+
    "\3\0\1\74\72\0\2\7\1\70\1\71\2\7\1\75"+
    "\1\0\2\7\1\76\2\7\1\77\2\7\1\100\33\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\14\7\1\101\2\7\1\102\14\7\1\103\1\104\6\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\34\7\1\105\1\106\6\7\1\0\1\107\1\110\6\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\17\7\1\102"+
    "\15\7\1\104\6\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\35\7\1\111\6\7\1\0\1\7"+
    "\1\110\6\7\20\0\2\7\1\70\1\71\2\7\1\75"+
    "\1\0\10\7\1\112\33\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\13\7\1\113\2\7\1\114"+
    "\25\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\12\7\1\115\1\116\2\7\1\117\2\7\1\120"+
    "\22\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\16\7\1\121\2\7\1\120\22\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\16\7\1\114"+
    "\25\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\23\7\1\122\1\123\17\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\26\7\1\124\1\125"+
    "\14\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\27\7\1\125\14\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\24\7\1\123\17\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\1\7"+
    "\1\126\1\7\1\127\2\7\1\130\2\7\1\131\16\7"+
    "\1\132\1\133\12\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\6\7\1\130\2\7\1\131\17\7"+
    "\1\133\12\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\14\7\1\134\2\7\1\135\16\7\1\136"+
    "\1\137\4\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\17\7\1\135\17\7\1\137\4\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\2\7\1\140\1\0"+
    "\2\7\1\141\23\7\1\142\1\143\4\7\1\144\1\145"+
    "\6\7\1\0\10\7\20\0\2\7\1\70\1\71\2\7"+
    "\1\140\1\0\27\7\1\143\5\7\1\145\6\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\1\7"+
    "\1\146\1\7\1\147\5\7\1\146\16\7\2\150\12\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\6\7\1\151\2\7\1\146\17\7\1\150\12\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\2\7\1\152\1\0"+
    "\2\7\1\153\41\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\2\7\1\152\1\0\44\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\13\7\1\154\2\7"+
    "\1\155\25\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\16\7\1\156\25\7\1\0\10\7\106\0"+
    "\1\157\104\0\1\160\2\0\1\157\101\0\1\161\11\0"+
    "\1\62\2\0\100\62\27\0\1\162\5\0\1\163\12\0"+
    "\1\164\34\0\1\165\1\0\1\65\2\0\77\65\1\0"+
    "\1\65\26\0\1\162\5\0\1\163\12\0\1\164\33\0"+
    "\1\166\6\0\1\167\2\7\1\70\1\71\3\7\1\0"+
    "\44\7\1\0\10\7\17\0\1\170\1\0\1\170\4\0"+
    "\1\170\2\0\43\170\1\0\10\170\24\0\2\72\1\74"+
    "\3\0\1\74\76\0\2\171\1\0\2\172\74\0\2\7"+
    "\1\70\1\71\3\7\1\0\6\7\1\173\35\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\3\7"+
    "\1\174\40\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\26\7\1\175\15\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\27\7\1\176\14\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\4\7\1\177\37\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\7\7\1\200\34\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\15\7\1\201"+
    "\22\7\1\202\3\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\20\7\1\203\20\7\1\204\2\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\15\7\1\205\26\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\20\7\1\206\23\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\44\7\1\0"+
    "\2\7\1\207\5\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\44\7\1\0\3\7\1\210\4\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\20\7\1\211\23\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\27\7"+
    "\1\212\14\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\14\7\1\213\27\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\17\7\1\214\24\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\12\7\1\215\31\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\32\7\1\216\11\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\33\7\1\217"+
    "\10\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\21\7\1\220\22\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\33\7\1\221\10\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\2\7"+
    "\1\222\41\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\2\7\1\223\1\0\44\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\44\7\1\0\6\7\1\224"+
    "\1\7\20\0\2\7\1\70\1\71\3\7\1\0\44\7"+
    "\1\0\7\7\1\225\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\4\7\1\226\37\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\3\7\1\227\40\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\6\7"+
    "\1\227\35\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\7\7\1\226\34\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\32\7\1\230\11\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\33\7\1\230\10\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\5\7\1\231\36\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\10\7\1\231"+
    "\33\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\3\7\1\232\40\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\6\7\1\233\35\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\11\7"+
    "\1\234\32\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\1\7\1\235\42\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\5\7\1\236\36\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\10\7\1\237\33\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\1\7\1\240\42\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\11\7\1\241"+
    "\32\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\34\7\1\242\1\243\6\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\35\7\1\243\6\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\10\7\1\244\33\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\5\7\1\245\36\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\14\7\1\246"+
    "\27\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\17\7\1\247\24\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\17\7\1\250\24\7\1\0"+
    "\10\7\20\0\2\167\1\251\1\0\3\167\1\0\44\167"+
    "\1\0\10\167\20\0\3\170\1\71\3\170\1\0\44\170"+
    "\1\0\10\170\24\0\2\171\77\0\2\7\1\70\1\71"+
    "\2\7\1\252\1\0\44\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\2\7\1\253\41\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\13\7"+
    "\1\254\30\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\16\7\1\255\25\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\26\7\1\256\15\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\27\7\1\257\14\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\34\7\1\260\7\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\2\7\1\261"+
    "\41\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\35\7\1\262\6\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\2\7\1\261\1\0\44\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\2\7\1\263"+
    "\41\7\1\0\10\7\20\0\2\7\1\70\1\71\2\7"+
    "\1\264\1\0\44\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\2\7\1\265\41\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\2\7\1\265\1\0\44\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\2\7\1\266"+
    "\1\0\44\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\16\7\1\267\25\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\15\7\1\270\26\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\20\7\1\270\23\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\1\7\1\271\42\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\2\7\1\272"+
    "\41\7\1\0\10\7\20\0\2\7\1\70\1\71\2\7"+
    "\1\273\1\0\44\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\11\7\1\274\32\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\2\7\1\275\1\0\44\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\13\7\1\276\30\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\16\7\1\277\25\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\34\7\1\300"+
    "\7\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\35\7\1\301\6\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\3\7\1\302\40\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\6\7"+
    "\1\302\35\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\7\7\1\303\34\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\4\7\1\303\37\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\2\7\1\304\41\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\2\7\1\304\1\0\44\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\5\7\1\305\36\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\10\7\1\306\33\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\40\7\1\307\3\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\41\7\1\310"+
    "\2\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\25\7\1\311\16\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\22\7\1\312\21\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\36\7"+
    "\1\313\5\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\37\7\1\314\4\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\37\7\1\315\4\7"+
    "\1\0\10\7\17\0\3\167\1\251\1\0\3\167\1\0"+
    "\44\167\1\0\10\167\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\7\7\1\316\34\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\4\7\1\317\37\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\5\7"+
    "\1\320\36\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\10\7\1\321\33\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\5\7\1\322\36\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\10\7\1\323\33\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\5\7\1\324\36\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\10\7\1\324"+
    "\33\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\1\7\1\325\42\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\1\7\1\326\7\7\1\326"+
    "\32\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\11\7\1\326\32\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\10\7\1\327\33\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\2\7"+
    "\1\330\41\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\13\7\1\331\30\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\16\7\1\332\25\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\2\7\1\333"+
    "\1\0\44\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\16\7\1\334\25\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\2\7\1\335\41\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\2\7\1\335"+
    "\1\0\44\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\30\7\1\336\13\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\31\7\1\337\12\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\34\7\1\340\7\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\35\7\1\341\6\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\2\7\1\342"+
    "\41\7\1\0\10\7\20\0\2\7\1\70\1\71\2\7"+
    "\1\342\1\0\44\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\2\7\1\343\1\0\44\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\2\7\1\344\41\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\44\7\1\0\2\7\1\345\5\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\44\7\1\0\3\7\1\346\4\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\44\7\1\0"+
    "\3\7\1\347\4\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\10\7\1\350\33\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\5\7\1\350\36\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\1\7"+
    "\1\351\42\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\11\7\1\352\32\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\34\7\1\353\7\7"+
    "\1\0\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\35\7\1\354\6\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\5\7\1\355\36\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\10\7\1\356"+
    "\33\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\11\7\1\357\32\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\5\7\1\360\36\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\44\7"+
    "\1\361\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\44\7\1\362\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\10\7\1\360\33\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\44\7\1\363\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\44\7\1\0\4\7"+
    "\1\364\3\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\44\7\1\0\5\7\1\364\2\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\30\7\1\365\13\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\31\7\1\366"+
    "\12\7\1\0\10\7\20\0\2\7\1\70\1\71\2\7"+
    "\1\367\1\0\44\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\2\7\1\370\41\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\44\7\1\371"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\44\7"+
    "\1\372\10\7\20\0\2\7\1\70\1\71\3\7\1\0"+
    "\44\7\1\373\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\22\7\1\374\21\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\22\7\1\375\2\7\1\375"+
    "\16\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\14\7\1\376\27\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\17\7\1\377\24\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\26\7"+
    "\1\u0100\15\7\1\0\10\7\20\0\2\7\1\70\1\71"+
    "\3\7\1\0\27\7\1\u0101\14\7\1\0\10\7\20\0"+
    "\2\7\1\70\1\71\3\7\1\0\25\7\1\375\16\7"+
    "\1\0\10\7\72\0\1\u0102\104\0\1\u0103\105\0\1\u0103"+
    "\31\0\2\7\1\70\1\71\3\7\1\0\4\7\1\u0104"+
    "\37\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\7\7\1\u0105\34\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\31\7\1\u0106\12\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\30\7"+
    "\1\u0106\13\7\1\0\10\7\72\0\1\u0107\104\0\1\u0108"+
    "\105\0\1\u0108\31\0\2\7\1\70\1\71\3\7\1\0"+
    "\34\7\1\u0109\7\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\35\7\1\u010a\6\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\30\7\1\u010b"+
    "\13\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\31\7\1\u010b\12\7\1\0\10\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\15\7\1\u010c\26\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\20\7"+
    "\1\u010d\23\7\1\0\10\7\75\0\1\u010e\105\0\1\u010e"+
    "\26\0\2\7\1\70\1\71\3\7\1\0\5\7\1\u010f"+
    "\36\7\1\0\10\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\10\7\1\u010f\33\7\1\0\10\7\75\0\1\u0110"+
    "\105\0\1\u0110\26\0\2\7\1\70\1\71\3\7\1\0"+
    "\5\7\1\u0111\36\7\1\0\10\7\20\0\2\7\1\70"+
    "\1\71\3\7\1\0\10\7\1\u0112\33\7\1\0\10\7"+
    "\20\0\2\7\1\70\1\71\3\7\1\0\44\7\1\0"+
    "\2\7\1\u0113\5\7\20\0\2\7\1\70\1\71\3\7"+
    "\1\0\44\7\1\0\3\7\1\u0113\4\7\20\0\2\7"+
    "\1\70\1\71\3\7\1\0\23\7\1\u0114\20\7\1\0"+
    "\10\7\20\0\2\7\1\70\1\71\3\7\1\0\24\7"+
    "\1\u0114\17\7\1\0\10\7\13\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[15732];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\1\1\1\11\2\1\1\11\2\1\2\11"+
    "\32\1\2\11\3\1\5\11\1\1\1\11\2\1\1\11"+
    "\2\1\1\0\2\1\1\0\62\1\10\11\3\1\1\0"+
    "\166\1\3\0\5\1\3\0\6\1\2\0\3\1\2\0"+
    "\5\1\1\11\1\1\1\11\4\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[276];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, -1, yychar, yytext());
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, -1, yychar, value);
  }

  private void scanError() throws QueryParseException {
    StringBuffer buf = new StringBuffer("Lexical Error: Illegal character <")
        .append(yytext()).append("> at offset ").append(yychar);
      throw new QueryParseException(buf.toString());
  }



  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Scanner(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Scanner(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1332) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 2: 
          { /* ignore */
          }
        case 59: break;
        case 51: 
          { return symbol(sym.HAVING);
          }
        case 60: break;
        case 24: 
          { return symbol(sym.IS);
          }
        case 61: break;
        case 47: 
          { return symbol(sym.WHERE);
          }
        case 62: break;
        case 44: 
          { return symbol(sym.DESC);
          }
        case 63: break;
        case 11: 
          { return symbol(sym.GT);
          }
        case 64: break;
        case 55: 
          { return symbol(sym.DISTINCT);
          }
        case 65: break;
        case 29: 
          { string.append('\t');
          }
        case 66: break;
        case 6: 
          { return symbol(sym.SUM);
          }
        case 67: break;
        case 10: 
          { return symbol(sym.LT);
          }
        case 68: break;
        case 15: 
          { string.setLength(0); yybegin(STRING);
          }
        case 69: break;
        case 57: 
          { return symbol(sym.TIMESTAMP);
          }
        case 70: break;
        case 48: 
          { return symbol(sym.ILIKE);
          }
        case 71: break;
        case 28: 
          { return symbol(sym.GTEQ);
          }
        case 72: break;
        case 21: 
          { return symbol(sym.DOUBLE, yytext());
          }
        case 73: break;
        case 39: 
          { return symbol(sym.NOT);
          }
        case 74: break;
        case 38: 
          { return symbol(sym.AND);
          }
        case 75: break;
        case 4: 
          { return symbol(sym.DIV);
          }
        case 76: break;
        case 19: 
          { string.append('\\');
          }
        case 77: break;
        case 30: 
          { string.append('\r');
          }
        case 78: break;
        case 52: 
          { return symbol(sym.BETWEEN);
          }
        case 79: break;
        case 50: 
          { return symbol(sym.OFFSET);
          }
        case 80: break;
        case 45: 
          { return symbol(sym.DATE);
          }
        case 81: break;
        case 36: 
          { return symbol(sym.ASC);
          }
        case 82: break;
        case 7: 
          { return symbol(sym.SUB);
          }
        case 83: break;
        case 41: 
          { return symbol(sym.TYPE);
          }
        case 84: break;
        case 3: 
          { return symbol(sym.IDENTIFIER, yytext());
          }
        case 85: break;
        case 33: 
          { string.append('\"');
          }
        case 86: break;
        case 37: 
          { return symbol(sym.ALL);
          }
        case 87: break;
        case 1: 
          { scanError();
          }
        case 88: break;
        case 54: 
          { return symbol(sym.ORDERBY);
          }
        case 89: break;
        case 20: 
          { yybegin(YYINITIAL);
                          return symbol(sym.STRING, string.toString());
          }
        case 90: break;
        case 32: 
          { string.append('\'');
          }
        case 91: break;
        case 40: 
          { return symbol(sym.LIKE);
          }
        case 92: break;
        case 22: 
          { return symbol(sym.OR);
          }
        case 93: break;
        case 25: 
          { return symbol(sym.IN);
          }
        case 94: break;
        case 9: 
          { return symbol(sym.EQ);
          }
        case 95: break;
        case 18: 
          { yybegin(YYINITIAL);
                          return symbol(sym.STRING,string.toString());
          }
        case 96: break;
        case 34: 
          { return symbol(sym.FROM_IDENTIFIER, yytext());
          }
        case 97: break;
        case 35: 
          { return symbol(sym.PATH_IDENTIFIER, yytext());
          }
        case 98: break;
        case 31: 
          { string.append('\n');
          }
        case 99: break;
        case 53: 
          { return symbol(sym.LOCATION);
          }
        case 100: break;
        case 27: 
          { return symbol(sym.LTEQ);
          }
        case 101: break;
        case 23: 
          { return symbol(sym.AS);
          }
        case 102: break;
        case 12: 
          { return symbol(sym.LPARA);
          }
        case 103: break;
        case 42: 
          { return symbol(sym.FROM);
          }
        case 104: break;
        case 49: 
          { return symbol(sym.SELECT);
          }
        case 105: break;
        case 13: 
          { return symbol(sym.RPARA);
          }
        case 106: break;
        case 58: 
          { return symbol(sym.STARTSWITH);
          }
        case 107: break;
        case 26: 
          { return symbol(sym.NOTEQ);
          }
        case 108: break;
        case 16: 
          { string.setLength(0); yybegin(SQ_STRING);
          }
        case 109: break;
        case 14: 
          { return symbol(sym.COMMA);
          }
        case 110: break;
        case 8: 
          { return symbol(sym.MUL);
          }
        case 111: break;
        case 56: 
          { return symbol(sym.GROUPBY);
          }
        case 112: break;
        case 17: 
          { string.append( yytext() );
          }
        case 113: break;
        case 5: 
          { return symbol(sym.INTEGER, yytext());
          }
        case 114: break;
        case 43: 
          { return symbol(sym.NULL);
          }
        case 115: break;
        case 46: 
          { return symbol(sym.LIMIT);
          }
        case 116: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}

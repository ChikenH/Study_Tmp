h1. Oracle Data Pump（データ・ポンプ）

* Oracle Database 10gから搭載された、コマンドで起動するエクスポート／インポート・ユーティリティ
* 論理バックアップ %{color: red}※Oracleは物理バックアップの補足としての使い方を推奨%
* 従来のexp/impよりも高速（サーバー側で実行され、dmpもサーバー側に格納される）

h3. メリット

* APIにより呼び出しが可能
* ジョブ管理・状況監視が可能
* 停止・再開が可能
* REMAP_DATAパラメータにより、ユーザが指定したPL/SQLファンクションでデータ変換可能（11gから）
* パラレル化が可能 %{color: red}※要Enterprise Edition%
* ダンプファイルの圧縮 %{color: red}※要Advanced Compression Option%
* ダンプファイルの暗号化 %{color: red}※要Advanced Security Option%

h3. 参照URL

"意外と知らない！？ Export/Importの基礎":https://blogs.oracle.com/oracle4engineer/exportimport-v2
"Data Pump(expdp/impdp)の使い方～エクスポート／インポート、データ移動、論理バックアップ":https://blogs.oracle.com/oracle4engineer/data-pumpexpdpimpdp
"Oracle® Databaseユーティリティ 12cリリース1(12.1)":https://docs.oracle.com/cd/E49329_01/server.121/b71303/toc.htm

---

h2. 前準備

* 実行するデータベース上のSYSユーザでの、Catexp.sql または catalog.sql スクリプトの実行
※通常はデータベース作成時に catalog.sql が実行されるので、改めて実行する必要はない

* 実行時に接続するデータベース・ユーザへの CREATE SESSION 権限の付与。
別のユーザが所有する表もエクスポートする場合には、 EXP_FULL_DATABASE ロールも付与

* 出力先ディレクトリの指定
<pre><code class="sql">
-- ディレクトリオブジェクト作成
SQL> CREATE DIRECTORY DPUMP_DIR1 AS '/home/oracle/oradata/dpump_dir';

-- 権限付与
SQL> GRANT READ,WRITE ON DIRECTORY dpump_dir1 TO scott;

COMMAND> expdp scott/tiger tables=emp,dept directory=dpump_dir1

/* パラメータでディレクトリを指定していない場合、OS参照変数"DATA_PUMP_DIR"、なければディレクトリオブジェクト"DATA_PUMP_DIR"を参照 */
-- 環境変数の設定
COMMAND> set DATA_PUMP_DIR=DPUMP_DIR1; export DATA_PUMP_DIR

COMMAND> expdp scott/tiger schemas=scott
</code></pre>

---

h2. Data Pumpの使い方 

h3. (1) Data Pump 実行前の準備

Data Pump による export/import を行う場合、ディレクトリ・オブジェクトの作成と権限の付与が必要。

* DIRECTORY OBJECT の作成そのディレクトリへのアクセス権限の付与
TEST_DIR というディレクトリを /home/test として作成し、SCOTTユーザが EXPDP/IMPDP を行う。
ディレクトリの作成と read/write 権限を付与。

<pre><code class="sql">
SQL> create or replace directory TEST_DIR as '/home/test';
SQL> grant read, write on directory TEST_DIR to SCOTT;
</code></pre>

* 権限の付与
CONNECTおよびRESOURCE ロールが必要。

h3. (2) Data Pump Export (EXPDP) の実行例 

<pre>
-- SCOTT ユーザのテーブル EMP を export する場合
COMMAND> expdp scott/tiger directory=test_dir tables=emp
</pre>

h3. (3) Data Pump Import (IMPDP) の実行例 

<pre>
-- SCOTT ユーザにテーブル EMP を import する場合
COMMAND> impdp scott/tiger directory=test_dir dumpfile=exp.dmp tables=emp
</pre>

---

h2. 主な、エクスポート／インポート対象を指定するオプション

|_.オプション|_.用途|
|TABLES|テーブル名を指定|
|SCHEMAS|スキーマ名を指定|
|TABLESPACES|表領域名を指定|
|FULL|FULL=Y でデータベース全体を指定|
|CONTENT||
|( CONTENT=data_only )|表のデータのみ|
|( CONTENT=metadata_only )|表の定義のみ|
|( CONTENT=all（デフォルト） )|表のデータと定義の両方|
|NOLOGFILE|NOLOGFILE=y で、ログファイルの出力を行わない|
|ESTIMATE_ONLY|ESTIMATE_ONLY=y で、領域の見積もりのみ実施|
|EXCLUDE|一部のオブジェクトを除く|

---

h2. インポートの際にインポート先を指定するオプション

インポートの際に、格納先を変更することが可能。

|_.オプション|_.説明|
|REMAP_SCHEMA|export 時と異なるスキーマに import|  
|REMAP_TABLESPACE|export 時と異なる表領域に import|
|REMAP_DATAFILE|export 時と異なる名前のデータファイルで import|
|SQLFILE|IMPDP 実行時に実行される SQL 文のコマンドを任意のファイルに出力|

<pre>
-- SCOTT ユーザの持つオブジェクトを TEST ユーザに import する場合
COMMAND> impdp scott/tiger directory=test_dir dumpfile=exp.dmp remap_schema=scott:test

-- USERS 表領域のオブジェクトを TESTTBS 表領域に import する場合
COMMAND> impdp scott/tiger directory=test_dir dumpfile=exp.dmp remap_tablespace=users:testtbs

-- USERS 表領域のデータファイル名を変更して import する場合
COMMAND> impdp system/manager directory=test_dir dumpfile=exp.dmp remap_datafile='/home/users.dbf:/home/test/users.dbf'

-- import 時に実行される SQL 文を確認する場合
COMMAND> impdp scott/tiger directory=test_dir dumpfile=exp.dmp sqlfile=test.sql
</pre>

h2. %{color: red}※SQLFILE を指定しても、オブジェクトが存在する場合は、"ORA-39208: ?????TABLE_EXISTS_ACTION?SQL_FILE??????????"が発生。
　SQLファイル出力ができない？%

---

---

h1. Export - Tips -

EXPDPで利用可能なモード。

|_.モード|_.機能説明|_.パラメータ|
|フル|データベース全体のエクスポート|FULL=y|
|スキーマ|指定したスキーマ全体のエクスポート|SCHEMAS=schema[,...]|
|テーブル|指定したテーブル全体のエクスポート|TABLES=table[,...]|
|テーブルスペース|指定したテーブルスペース全体のエクスポート|TABLESPACES=tablespace[,...]|

<pre>
-- フルモード
COMMAND> expdp scott/tiger full=y

-- テーブルモード
COMMAND> expdp scott/tiger dumpfile=exp.dmp tables=emp,dept

-- スキーマモード（ディレクトリ指定）
COMMAND> expdp scott/tiger directory=test_backup schemas=scott dumpfile=scott_dpump`date +%y%m%d`.dump logfile=scott_dpump`date +%y%m%d`.log
</pre>

---

h3. フラッシュバック・モードの指定

指定したSCNもしくは時刻における一貫性を維持したデータのエクスポートが可能。

<pre>
-- 時刻指定 ※TO_TIMESTAMP()で時刻を指定。エスケープ文字が必要となるので、下記はパラメータファイルで指定
COMMAND> cat parfile.txt
FLASHBACK_TIME="TO_TIMESTAMP('2004/03/20 10:00','YYYY/MM/DD HH:MI')"

COMMAND> expdp scott/tiger parfile=parfile.txt

-- パラメータファイル使用せずに時刻指定する FLASHBACK_TIME の設定例
flashback_time=\"TO_TIMESTAMP\(TO_CHAR\(SYSDATE,\'YYYY-MM-DD HH24:MI:SS\'\),\'YYYY-MM-DD HH24:MI:SS\'\)\"

-- SCN（System Change Number）指定
COMMAND> expdp scott/tiger flashback_scn=364909
</pre>

---

h3. パラレル・エクスポートの指定 %{color: red}※要Enterprise Edition%

エクスポート処理をパラレルで行うことが可能。

<pre>
-- 並列度の指定　※置き換え変数（%U）を指定しない場合、dumpfileのファイル数はparallelの値に合わせる必要あり
COMMAND> expdp scott/tiger parallel=3 dumpfile=dpump_dir1:expdat%U.dmp,dpump_dir2%U.dmp

-- 生成されたダンプファイルセットの確認
COMMAND> ls -lR
dpump_dir1:
......expdat01.dmp
......expdat02.dmp
dpump_dir2:
......expdat01.dmp
</pre>

---

h3. ダンプファイルの見積もり

ダンプファイルを生成せず、生成されるダンプファイルのサイズを見積もり可能。

|_.BLOCKS|ブロックサイズ × オブジェクトのブロック数（精度高くない）|
|_.STATISTICS|統計情報を元に見積もり|

<pre>
-- 見積もりのみ出力する場合には、ESTIMATE_ONLY=y を指定
COMMAND> expdp scott/tiger tables=emp,dept estimate=blocks estimate_only=y
</pre>

---

---

h1. Import - Tips -

IMPDPでは、EXPDPで使用できたパラメータのほとんどを利用可能。

---

h3. ネットワーク・リンクを使用（network_link）

データベース・リンクを使用して、リモートデータベースから直接ローカルデータベースにインポートを行うことが可能。

%{color: red}エクスポートのネットワーク・リンク指定は、リモートデータベースのデータをローカルにエクスポートする機能。
インポートの場合には、直接ローカルデータベースに書き込むので、ダンプファイルを作成しない。%

<pre>
/* 接続するリモートデータベースを、NETWORK_LINK パラメータで指定。指定する値はデータベース・リンク名 */
-- DB LINK の指定
COMMAND> impdp scott/tiger tables=emp,dept directory=dpump_dir network_link=scott.jp.oracle.com
</pre>

---

h3. SQL文を受け取る（sqlfile）

インポートが他のパラメータに基づいて実行する全てのSQL DDLの書き込み先のファイルを指定する。
※パスワードはSQLファイルには含まれない。例）DLLにCONNECT文あり → コメントで置き換えられ、スキーマ名のみ表示

<pre>
-- TEST_TABLE表をインポートする際の SQLFILE を受け取る
COMMAND> impdp oradirect/oradirect dumpfile=test_table.dmp tables=test_table sqlfile=sqlfile.txt
</pre>

---

h3. 作成しようとしている表がすでに存在する場合（table_exists_action）

インポート・ユーティリティに対して、作成しようとしている表がすでに存在する場合に行う操作を指定します。

|_.SKIP|表はそのままにして、次のオブジェクトに移動。
CONTENTパラメータがDATA_ONLYに設定されている場合、このオプションは無効|
|_.APPEND|ソースから行をロードし、既存の行は変更しない|
|_.TRUNCATE|既存の行を削除した後、ソースから行をロード|
|_.REPLACE|既存の表を削除した後、ソースから表を作成およびロード。
CONTENTパラメータがDATA_ONLYに設定されている場合、このオプションは無効|

%{color: red}デフォルトはSKIP。
CONTENT=DATA_ONLYが指定されている場合、デフォルトはSKIPではなくAPPEND)%

<pre>
impdp hr TABLES=employees DIRECTORY=dpump_dir1 DUMPFILE=expfull.dmp TABLE_EXISTS_ACTION=REPLACE
</pre>

h2. %{color: red}※オブジェクトが存在する場合は、ORA-31684が多発するため、スキーマ内の全オブジェクトを全削除してから実行するのが無難%

---

---

h1. Oracle RMAN（Recovery Manager）

* Oracle Databaseでバックアップおよびリカバリ・タスクを実行し、バックアップ計画の管理を自動化するユーティリティ
* 専用のRMANコマンドを使用して操作
* OSプロンプトからRMANを起動してコマンドラインで実行、またはOracle Enterprise Manager（EM）のGUIを使用して実行

h3. バックアップ方法

||_. 一貫性バックアップ（コールドバックアップ） |_. 非一貫性バックアップ（ホットバックアップ） |
|_.方式|データベースがオープンされていない状態でファイルをコピー
|インスタンス起動中にファイルをコピー
%{color: red}※アーカイブログモードで運用している場合のみ取得可能%|
|_.復旧|正常にシャットダウンした後（showdown abort以外）にバックアップを取得しておけば、
ファイルをリストアするだけでバックアップ取得時点に復旧可能
|取得したバックアップは一貫性を持たない状態であるため、リカバリを行って一貫性を復元する処理が必要|
|_.適用|夜間や休日に停止するシステム。
停止後マウント状態で起動してから一貫性バックアップを取得
|24時間稼動のシステム。
夜間のアクセスが少ない時間帯に非一貫性バックアップを取得|

h3. メリット

* バックアップ・ファイルの形式として、OS上のファイルと同等の「イメージ・コピー」と、未使用領域を含まない効率的な「バックアップ・セット」（RMAN独自形式）を選択可能。
 全体バックアップや増分（差分増分/累積増分）バックアップ等、様々なバックアップの取得も可能

* 設定した保存方針に従い、バックアップの管理をOracle Database自身が行う。
手動で行うバックアップの複雑さ、人為的ミスを最小限に抑え、確実なバックアップ管理を低コストで実現

* Oracle Databaseがリカバリに必要なバックアップを自動的に選択。
データ・リカバリ・アドバイザが、障害の診断および修復方法を提示。
障害復旧の大半を占める、障害解析に要する時間を削減し、MTTR（平均リカバリ時間）の大幅短縮が実現

* 破損ブロックの検証、未使用データの圧縮（スキップ）、暗号化、ブロックメディア・リカバリ等々、Oracle Databaseのバックアップ・リカバリを効率的に実施する仕組みを利用可能
%{color: red}※Enterprise Editionのみ利用可能、もしくはOptionが必要な機能あり%

h3. 参照URL

"RMAN(Recovery Manager)の使い方～バックアップ、リカバリ/リストア、コマンド、接続方法":https://blogs.oracle.com/oracle4engineer/rmanrecovery-manager
"実践!! バックアップ・リカバリ ～ユーザー手動 VS RMAN コマンドライン対決～":https://blogs.oracle.com/oracle4engineer/vs-rman
"最低限実施すべきRMAN設定":http://cosol.jp/tech/detail/d3_highly_recommended_rman_configurations.shtml
"Oracle® Databaseバックアップおよびリカバリ・ユーザーズ・ガイド 12cリリース1(12.1)":https://docs.oracle.com/cd/E57425_01/121/BRADV/toc.htm

h3. 参考書籍

"プロトしてのOracle運用管理入門（初版）":https://www.amazon.co.jp/dp/4797355123

---

h2. RMANの基本コマンド

コマンドラインで実行する基本的なコマンドについて説明。

h3. (1) RMANに接続

管理者権限（SYSDBA権限）で接続
<pre>
% rman target sys/sysのパスワード
</pre>

%{color: red}※Oracle管理用のOSユーザであれば、SYSユーザでなくてもOS認証で操作可能%
<pre>
% rman target /
</pre>

h3. (2) 設定の表示、初期設定

- show（表示）
%{color: red}※デフォルト値には、末尾に "# default" と表示%

<pre>
# バックアップに関する設定の確認
RMAN> show all;

db_unique_name VM30のデータベースにおけるRMAN構成パラメータ:
CONFIGURE RETENTION POLICY TO REDUNDANCY 1; # default
CONFIGURE BACKUP OPTIMIZATION OFF; # default
CONFIGURE DEFAULT DEVICE TYPE TO DISK; # default
CONFIGURE CONTROLFILE AUTOBACKUP ON;
CONFIGURE CONTROLFILE AUTOBACKUP FORMAT FOR DEVICE TYPE DISK TO '%F'; # default
CONFIGURE DEVICE TYPE DISK BACKUP TYPE TO COPY PARALLELISM 1;
CONFIGURE DATAFILE BACKUP COPIES FOR DEVICE TYPE DISK TO 1; # default
CONFIGURE ARCHIVELOG BACKUP COPIES FOR DEVICE TYPE DISK TO 1; # default
CONFIGURE MAXSETSIZE TO UNLIMITED; # default
CONFIGURE ENCRYPTION FOR DATABASE OFF; # default
CONFIGURE ENCRYPTION ALGORITHM 'AES128'; # default
CONFIGURE COMPRESSION ALGORITHM 'BASIC' AS OF RELEASE 'DEFAULT' OPTIMIZE FOR LOAD TRUE ; # default
CONFIGURE ARCHIVELOG DELETION POLICY TO NONE; # default
CONFIGURE SNAPSHOT CONTROLFILE NAME TO '/app/oracle/product/11.2.0/dbhome_1/dbs/snapcf_vm30.f'; # default
</pre>

- configure（初期設定）

バックアップファイルの出力先設定
%{color: red}※出力先をデータベース構成ファイルを配置したディスクとは別のディスク上のディレクトリに設定し、ディスク障害に対応。
　10g以降は高速リカバリ領域（フラッシュリカバリ領域）に出力を前提とした機能があり、高速リカバリ領域使用時にはディレクトリパスを含めない設定を推奨%

主なFORMAT指定子
|_.構文要素|_.説明|
|%U|Oracleによって生成される一意のファイル名(デフォルト)|
|%d|データベースの名前|
|%D|現在の月の日付（DD形式）|
|%e|アーカイブ・ログのログ順序番号|
|%F|DBID、日、月、年および順序を組み合せて生成される名称|
|%N|表領域名。※データファイルをイメージ・コピーとしてバックアップする場合のみ有効|
|%s|バックアップセット番号|
|%t|バックアップセットのタイムスタンプ|
|%T|現在の年、月および日（YYYYMMDD形式）|

<pre>
CONFIGURE CHANNEL DEVICE TYPE DISK FORMAT '/disk1/%U';
</pre>

保存ポリシーの設定　※併用不可

<pre>
# リカバリ期間ベース（リカバリ可能な過去の最大日数を基準）
CONFIGURE RETENTION POLICY TO RECOVERY WINDOW OF <日数> DAYS;

# 冗長性ベース（バックアップファイルの数を基準）
CONFIGURE RETENTION POLICY TO REDUNDANCY <冗長数>;
</pre>

バックアップファイルの最適化（バックアップ済みのファイルをスキップ）

<pre>
CONFIGURE BACKUP OPTIMIZATION ON;
</pre>

制御ファイルのバックアップを有効化

<pre>
RMAN> CONFIGURE CONTROLFILE AUTOBACKUP ON;
</pre>

バックアップ処理のパラレル化 %{color: red}※要Enterprise Edition？（少なくとも11gR1までは）%

<pre>
CONFIGURE DEVICE TYPE [DISK|SBT] PARALLELISM <並列度>;
</pre>

バックアップファイルの圧縮（デフォルトのみ）
%{color: red}※Oracle Advanced Compressionオプションを有効にすると、下表の圧縮レベルを選択可。
　基本的に圧縮率はLOWからHIGHへと高くなるが、消費されるCPUリソースも比例する。%

|_.圧縮レベル|_.パフォーマンスのメリットとデメリット|
|HIGH|ネットワーク速度が制限事項となる、速度の遅いネットワークでのバックアップに最適|
|MEDIUM|ほとんどの環境で推奨。圧縮率と速度の優れた組合せ|
|LOW|バックアップ・スループットへの影響が最小|

<pre>
CONFIGURE COMPRESSION ALGORITHM 'BASIC';
</pre>

バックアップファイルの暗号化（バックアップセット形式のみ）

<pre>
CONFIGURE ENCRYPTION FOR DATABASE ON;
</pre>

バックアップ暗号化アルゴリズムの構成
%{color: red}※指定可能な値は、V$RMAN_ENCRYPTION_ALGORITHMSで有効なもの%

<pre>
CONFIGURE ENCRYPTION ALGORITHM TO 'AES256';
</pre>

設定値をデフォルト値に戻す

<pre>
CONFIGURE <設定項目> CLEAR;
</pre>


h3. (3) backup（バックアップ実行）

* バックアップセットのバックアップ

<pre>
# データベース全体
RMAN> BACKUP DATABASE;
</pre>

<pre>
# 表領域単位
RMAN> BACKUP TABLESPACE users;
</pre>

<pre>
# データファイル単位
RMAN> BACKUP DATAFILE 1,2
</pre>

* イメージ・コピーのバックアップ

<pre>
# データベース全体
RMAN> BACKUP AS COPY DATABASE;
</pre>

<pre>
# データファイル単位
RMAN> BACKUP AS COPY DATAFILE 1,2;
</pre>

* 増分バックアップ

%{color: red}※基本的な運用
- 週1回レベル0の増分バックアップを取得
- 毎日、差分増分バックアップ、または、累積増分バックアップを取得%

|_.レベル|_.タイプ|_.説明|_.形式|
|0|-|基礎となるバックアップ。全てのブロックをバックアップするため、ファイルサイズは通常のバックアップと同等|バックアップセット、イメージコピーどちらでも可|
|1|差分増分|前回の増分バックアップ（レベル0、1の両方）以後の増分をバックアップ|バックアップセットのみ|
|1|累積増分|前回の増分バックアップ（レベル0のみ）以後の増分をバックアップ|バックアップセットのみ|

<pre>
# レベル0（バックアップセット）
RMAN> BACKUP INCREMENTAL LEVEL 0 DATABASE;
</pre>

<pre>
# レベル0（イメージコピー）
RMAN> BACKUP AS COPY INCREMENTAL LEVEL 0 DATABASE;
</pre>

<pre>
# レベル1の差分増分
RMAN> BACKUP INCREMENTAL LEVEL 1 DATABASE;
</pre>

<pre>
# レベル１の累積増分
RMAN> BACKUP INCREMENTAL LEVEL 1 CUMULATIVE DATABASE;
</pre>

h3. (4) restore、recovery（バックアップ実行）

* データベース全体のリストア、リカバリ 

<pre>
RMAN> SHUTDOWN;
RMAN> STARTUP MOUNT;
RMAN> RESTORE DATABASE;
RMAN> RECOVER DATABASE;
RMAN> ALTER DATABASE OPEN;
</pre>

* 表領域単位のリストア、リカバリ %{color: red}※DATAFILEへのRESTORE、RECOVERも同様のコマンドを実行%

<pre>
RMAN> sql "alter tablespace users offline";
RMAN> sql "alter tablespace data offline";
RMAN> RESTORE TABLESPACE USERS, DATA;
RMAN> RECOVER TABLESPACE USERS, DATA;
RMAN> sql "alter tablespace users online";
RMAN> sql "alter tablespace data online";
</pre>

---

h2. バックアップファイルの管理

h3. 取得済みのバックアップ確認

<pre>
RMAN> LIST BACKUP;
</pre>

h3. 不要ファイルの削除

オプション
|_.RECOVERY WINDOW OF <日数> DAYS|リカバリ期間ベース|
|_.REDUNDANCY <冗長数>|冗長性ベース|

<pre>
RMAN> DELETE OBSOLETE;
</pre>

---

h2. データ・リカバリ・アドバイザ

障害の自動検知と通知、および復旧手順のアドバイス機能により、原因究明、解析時間を最小化することができる。

<pre>
# 1.データベースの障害リストを参照
RMAN> list failure;

# 2.障害に対するアドバイスを取得
RMAN> advise failure;

# 3.自動修復の実行
RMAN> repair failure;
</pre>
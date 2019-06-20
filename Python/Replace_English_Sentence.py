# coding: utf-8

'''
 標準入力の英文を、読みやすいように整理する
'''
import sys

# 複数行の標準入力をリスト化
def inputToList():
    listInput = []

    # 標準入力を 1 行ずつリストに追加
    for line in sys.stdin:
        listInput.append(line)

    return listInput

# リスト内の指定文字を置換して、1 文字列に整理する
def replaceText(lists, strMark, strCode, flgBlankLine):
    strRtn = ""

    # 末尾 strMark での改行フラグ
    flgLF = 0

    for strBuf in lists:
        # 空行かそうでないかで処理を分岐
        if strBuf == strCode:
            # flgBlankLine が 1:on 以外の場合、空行はとばす
            if not flgBlankLine == 1:
                continue
            # 改行フラグが 0:off の場合は、前行の改行が後の処理で削除されるため追加
            elif flgLF == 0:
                strBuf += strCode
        else:
            # 改行コードを削除
            strBuf = strBuf.replace(strCode, "")

            # 改行コードの前が strMark でない場合は、文字が繋がってしまうため、空白を追加
            if not strBuf[-1] == strMark:
                strBuf += " "
                flgLF = 0
            else:
                # 改行フラグを 1:on
                flgLF = 1

        # strMark を基準に、strCode を付与して改行
        strRtn += strBuf.replace(strMark, strMark + strCode)

    return strRtn

# リスト化された標準入力を取得
listInputs = inputToList()
# print(listInputs)

# 改行の基点
strBlankMark = "."

# 改行コード
strLFCode = "\n"

# 改行のみの出力制御フラグ (1:on)
flgBlankLine = 0

# 整理後の英文を出力
strOutputs = replaceText(listInputs, strBlankMark, strLFCode, flgBlankLine)
print(strOutputs)

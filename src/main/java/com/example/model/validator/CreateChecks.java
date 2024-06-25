package com.example.model.validator;

import jakarta.validation.groups.Default;

/**
 *  バリデーショングループの設定
 *  CreateChecksグループはDefaultグループを継承、
 *  この結果、CreateChecksグループが有効なときは
 *  Defaultグループも有効となります。
 */
public interface CreateChecks extends Default{

}

import locale from '@aligov/global-locale';
import stringFormat from '@aligov/global-string-format';
import strings from './strings';
let language; 
let intl;
function update() {
  const { lang } = locale.getLocale();
  language = lang;
  intl = stringFormat.init(lang, strings);
}

function change(langTag) {
  locale.setLang(langTag);
  update();
}
function get(id: any, variable?: any) {
  if (!intl) update();
  if (typeof id === 'string') {
    return stringFormat.format(
      {
        id: id,
      },
      variable,
    );
  } else if (typeof id === 'object' && id.dm) {
    id.defaultMessage = id.dm;
  }
  return stringFormat.format(
    {
      id: id.id,
      defaultString: id.dm,
    },
    variable,
  );
}

export default {
  get,
  update,
  change,
  language,
};

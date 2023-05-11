export default function addThemeClassName() {
  const rootName: any = document.body;
  let theme = '';
  rootName.setAttribute('theme', 'holoDark');
  return theme;
}

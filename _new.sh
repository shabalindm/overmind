read -p "name: " name
cp /git/overmind/_support/template.md  $name.md
echo $name.md created
mkdir _/$name
cp /git/overmind/_support/template.png _/$name/1.png

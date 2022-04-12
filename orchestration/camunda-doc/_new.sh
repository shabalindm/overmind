project=/git/overmind
read -p "name: " name
cp ${project}/_support/template.md  $name.md
echo $name.md created
mkdir ${project}/_/$name
cp ${project}/_support/template.png ${project}/_/$name/1.png
